package org.vaadin.firitin;

import com.helger.commons.mutable.MutableInt;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.TreeTable;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Route
public class TreeTableView extends VerticalLayout {
    static Path root = Path.of("./src/main/java");

    public TreeTableView() {
        TreeTable<Path> treeGrid = new TreeTable<>();
        // Lazy loading works as usually in Grid,
        // just don't use DataProvider, which you never should ;-)
        /*
         * Note, lazy loading file system like this is probably
         * bad idea, and caching to memory would be more efficient,
         * but written here as an academical example...
         */
        boolean lazyload = false;
        if (lazyload) {
            MyTreeModel model = new MyTreeModel();
            treeGrid.setTreeTableModel(model);
            treeGrid.setItems(q -> {
                final int offset = q.getOffset();
                final int limit = q.getLimit();
                System.out.println("query for: offset" + offset + " l" + limit);
                return MyService.streamPaths(offset, limit, model);
            });
        } else {
            // Alternative API without lazy loading. Uses more memory, but simpler
            // treeGrid.setOpenModel(new TreeTable.ClosedByDefault<>());
            List<Path> rootItems = MyService.rootItems();
            treeGrid.setRootItems(rootItems, p -> {
                try {
                    return Files.list(p).toList();
                } catch (IOException e) {
                    return Collections.emptyList();
                }
            });

        }

        //Grid.Column<Path> hierarchyColumn = treeGrid.addHierarchyColumn(p -> p.getFileName().toString()).setHeader("File name");
        Grid.Column<Path> hierarchyColumn = treeGrid.addHierarchyComponentColumn(p -> {
                    Text filename = new Text(p.getFileName().toString());
                    var preview = new Button("preview...", e -> previewContent(p));
                    preview.setEnabled(treeGrid.getLeafModel().isLeaf(p));
                    return new VHorizontalLayout(
                            (treeGrid.getLeafModel().isLeaf(p) ? VaadinIcon.FILE.create() : VaadinIcon.FOLDER.create()),
                            filename,
                            preview
                    ).withAlignItems(Alignment.CENTER);
                }
        ).setHeader("File name");
        treeGrid.addColumn(Path::toString).setHeader("Full path");
        treeGrid.addColumn(p -> {
            return treeGrid.getLeafModel().isLeaf(p) ? "" + "File" : "Directory";
        }).setHeader("Type");
        treeGrid.addColumn(p -> {
            return "" + treeGrid.getLevelModel().getLevel(p);
        }).setHeader("Depth");
        treeGrid.addColumn(p -> p.toFile().length()).setHeader("Size");

        // Do the usual grid stuff...
        treeGrid.getColumns().forEach(c -> {
            c.setFlexGrow(1);
            c.setResizable(true);
        });
        hierarchyColumn.setFlexGrow(4);

        add(treeGrid);

        add(new Button("Scroll to item", e -> {
            treeGrid.scrollToItem(root.resolve("org/vaadin/firitin/components/TreeTable.java"));
        }));

    }

    private void previewContent(Path p) {
        try {
            String s = Files.readString(p, StandardCharsets.UTF_8);
            if (s.length() > 1000) {
                s = s.substring(0, 1000) + "...";
            }
            Notification.show("Previewing " + p.toFile() + ":\n" + s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class MyService {

        /**
         * Note, using OpenModel here just because I'm lazy,
         * don't mix backend with your UI code in real life!
         *
         * @param offset
         * @param limit
         * @param model
         * @return
         */
        public static Stream<Path> streamPaths(int offset, int limit, TreeTable.OpenModel<Path> model) {
            ArrayList<Path> page = new ArrayList<>();
            MutableInt skipped = new MutableInt(0);
            try {
                Files.walkFileTree(root, new FileVisitor<>() {

                    // TODO sort files by name: collect full directory first
                    // to a temp file, then sort it, then add files
                    // Will leave it in "whatever order" for now
                    // seems to be stable at least on mac

                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

                        boolean open = dir == root || model.isOpen(dir);

                        if (skipped.intValue() < offset) {
                            skipped.inc();
                            return open ? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
                        }
                        if (page.size() >= limit) {
                            return FileVisitResult.TERMINATE;
                        }
                        if (dir != root) {
                            page.add(dir);
                        }
                        return open ? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if (skipped.intValue() < offset) {
                            skipped.inc();
                            return FileVisitResult.CONTINUE;
                        }
                        if (page.size() >= limit) {
                            // Already enough in the page
                            return FileVisitResult.TERMINATE;
                        }
                        page.add(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }
                });
                return page.stream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public static List<Path> rootItems() {
            try {
                return Files.list(root).toList();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * Provides and saves tree related details about the items in the tree.
     * In a real world implementation, these could come/go to a database.
     * OpenModel<T>, LeafModel<T>, DepthModel<T> can also be provided separately,
     * OpenModel is optional.
     */
    public static class MyTreeModel implements TreeTable.TreeTableModel<Path> {

        private Set<Path> closed = new HashSet<>();
        private Path root = Path.of("./src/main/java");

        /*
         * Note, for OpenModel there is a default in-memory
         * implementation. In some cases
         * might be simpler to provide only LeafModel and DepthModel.
         */
        @Override
        public boolean isOpen(Path item) {
            return !closed.contains(item);
        }

        @Override
        public void setOpen(Path item, boolean open) {
            if (open) {
                closed.remove(item);
            } else {
                closed.add(item);
            }
        }

        @Override
        public boolean isLeaf(Path item) {
            return !Files.isDirectory(item);
        }

        @Override
        public int getLevel(Path item) {
            int depth = 0;
            Path parent = item.getParent();
            while (!parent.equals(root)) {
                depth++;
                parent = parent.getParent();
            }
            return depth;
        }
    }
}

package org.vaadin.firitin.appframework;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import org.vaadin.firitin.components.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Same order as with MyFirstView.java, but title (derived from classname) after it alphabetically
@MenuItem(order = MenuItem.BEGINNING)
public class MySecondView extends MyAbstractView {
    public MySecondView() {
        add(new Paragraph("Second content"));

        add(new Button("Toggle fourth", e-> {
            BasicNavigationItem item = (BasicNavigationItem) findAncestor(MyMainLayout.class).getNavigationItems().get(3);
            item.setEnabled(!item.isEnabled());
        }));

        add(new Button("Component tree with HasComponents", e-> {
            new ComponentTreeWithHasComponents(UI.getCurrent());
        }));

        add(new Button("Component tree with new helper", e-> {
            new ComponentTreeWithNewHelper(UI.getCurrent());
        }));

        add(new Button("Component tree with ComponentUtil", e-> {
            new ComponentTreeWithComponentUtil(UI.getCurrent());
        }));

        add(new Button("Direct children of this via VHasComponent.children", e-> {
            String children = children().map(component -> component.getClass().getSimpleName()).collect(Collectors.joining(","));
            Notification.show("Direct children: " + children);
        }));

    }

    public class ComponentTreeWithHasComponents extends Dialog {
        public ComponentTreeWithHasComponents(UI ui) {
            super();
            setHeaderTitle("Component Tree (with HasComponents):");
            add(componentTree(ui,
                    c -> c instanceof HasComponents hc ? hc.getChildren().toList() : List.of()));
            open();
        }
    }

    public class ComponentTreeWithNewHelper extends Dialog {
        public ComponentTreeWithNewHelper(UI ui) {
            super();
            setHeaderTitle("Component Tree (with Element API):");
            add(componentTree(ui, c -> ComponentUtil.getAllChildren(c).toList()));
            open();
        }
    }

    public class ComponentTreeWithComponentUtil extends Dialog {
        public ComponentTreeWithComponentUtil(Component ui) {
            super();
            setHeaderTitle("Component Tree (with Element API):");
            add(componentTree(ui, c -> ComponentUtil.getChildren(c).toList()));
            open();
        }
    }

    private static Tree<Component> componentTree(Component root,
            Tree.ChildrenProvider<Component> childrenProvider) {
        Tree<Component> tree = new Tree<>();
        tree.setItemLabelGenerator(MySecondView::describe);
        tree.setItems(root, childrenProvider);
        tree.showChildrenRecursively(root);
        return tree;
    }

    private static String describe(Component component) {
        String name = component.getClass().getName();
        List<String> details = new ArrayList<>();
        component.getId().ifPresent(id -> details.add("id=" + id));
        if (component instanceof HasLabel hl) {
            String label = hl.getLabel();
            if (label != null && !label.isEmpty()) {
                details.add("label=\"" + truncate(label) + "\"");
            }
        }
        if (component instanceof HasText ht) {
            String text = ht.getText();
            if (text != null && !text.isEmpty()) {
                details.add("text=\"" + truncate(text) + "\"");
            }
        }
        return details.isEmpty() ? name : name + " (" + String.join(", ", details) + ")";
    }

    private static String truncate(String s) {
        int max = 40;
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }
}

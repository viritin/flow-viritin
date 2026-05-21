package org.vaadin.firitin.appframework;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Paragraph;

import com.vaadin.flow.component.notification.Notification;
import org.vaadin.firitin.components.Tree;
import org.vaadin.firitin.fluency.ui.FluentHasComponents;
import org.vaadin.firitin.util.ComponentTree;

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

        add(new Button("Component tree with Element API", e-> {
            new ComponentTreeWithStateNode(UI.getCurrent());
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

    public class ComponentTreeWithStateNode extends Dialog {
        public ComponentTreeWithStateNode(UI ui) {
            super();
            setHeaderTitle("Component Tree (with Element API):");
            add(componentTree(ui, c -> ComponentTree.children(c).toList()));
            open();
        }
    }


    private static Tree<Component> componentTree(Component root,
            Tree.ChildrenProvider<Component> childrenProvider) {
        Tree<Component> tree = new Tree<>();
        tree.setItemLabelGenerator(c -> c.getClass().getSimpleName());
        tree.setItems(root, childrenProvider);
        tree.showChildrenRecursively(root);
        return tree;
    }
}

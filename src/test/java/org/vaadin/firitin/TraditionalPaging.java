package org.vaadin.firitin;

import org.vaadin.firitin.components.grid.PagingGrid;
import org.vaadin.firitin.testdomain.Person;
import org.vaadin.firitin.testdomain.Service;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.Route;

/**
 * A prototype of a Grid with built-in paging widget and support for huge
 * collections. Reporting size is optional.
 *
 * @author Matti Tahvonen
 */
@Route
public class TraditionalPaging extends VerticalLayout {

	public TraditionalPaging() {
		final PagingGrid<Person> table = new PagingGrid<>(Person.class);

		// Define results with a simpler data provider API, that just gives you page to
		// request
		table.setPagingDataProvider((page, pageSize) -> {
			// This is demo specific line, normally, e.g. with spring data, page number is
			// enough
			int start = (int) (page * table.getPageSize());
			// Optional, sorting
			if (!table.getSortOrder().isEmpty()) {
				GridSortOrder<Person> sortOrder = table.getSortOrder().get(0);
				String propertyId = sortOrder.getSorted().getKey();
				boolean asc = sortOrder.getDirection() == SortDirection.ASCENDING;
				return Service.findAll(start, pageSize, propertyId, asc);
			}
			return Service.findAll(start, pageSize);
		});

		// Optional
		// If you know, or some further long running task can detect the size of
		// results, it can be defined later:
		Button b = new Button("Define size to 1000", e -> {
			table.setTotalResults(1000);
		});
		add(table, b);
	}

}

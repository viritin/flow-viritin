package org.vaadin.firitin;

import java.util.List;

import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.grid.VGrid;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.testdomain.Person;
import org.vaadin.firitin.testdomain.Service;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.event.SortEvent;
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

	public static class PagingGrid<T> extends VGrid<T> {

		public interface PagingDataProvider<T> {
			List<T> pageRequested(long page);
		}

		private PagingDataProvider<T> dataProvider;
		private PagingGrid<T>.PaginationBar paginationBar;
		private ComponentEventListener<SortEvent<Grid<T>, GridSortOrder<T>>> sortListener = event -> {
			// scroll to the beginning and fetch new rows (up to the implementation to read
			// sort order from the grid)
			setItems(dataProvider.pageRequested(paginationBar.currentPage));
		};

		public PagingGrid() {
			super();
			addSortListener(this.sortListener);
		}

		public PagingGrid(Class<T> beanType) {
			super(beanType);
			addSortListener(this.sortListener);
		}

		public PagingGrid(int pageSize) {
			super(pageSize);
			addSortListener(this.sortListener);
		}

		private void preparePaginationBar() {
			paginationBar = new PaginationBar(null);
			HeaderRow headerRow = prependHeaderRow();
			headerRow.join(getColumns().toArray(new Column[0])).setComponent(paginationBar);
		}

		public PagingDataProvider<T> getPagingDataProvider() {
			return dataProvider;
		}

		public void setPagingDataProvider(PagingDataProvider<T> listener) {
			this.dataProvider = listener;
			preparePaginationBar();
			setItems(dataProvider.pageRequested(0));
		}

		class PaginationBar extends VHorizontalLayout {

			private static final long serialVersionUID = 7799263034212965499L;

			private void updateState() {

				final boolean hasPrev = currentPage > 0;
				first.setEnabled(hasPrev);
				previous.setEnabled(hasPrev);
				if (sizeKnown()) {
					final boolean hasNext = currentPage < pages - 1;
					last.setVisible(true);
					last.setEnabled(hasNext);
					next.setEnabled(hasNext);
					status.setText("Page " + (currentPage + 1) + "/" + pages + ", showing " + getPageSize()
							+ " results per page.");
				} else {
					last.setEnabled(false);
					next.setEnabled(true);
					status.setText("Page " + (currentPage + 1) + ", showing " + getPageSize() + " results per page.");
				}
			}

			private void initButtons() {
				first = new VButton(VaadinIcon.FAST_BACKWARD.create(), e -> handleClick(e));
				last = new VButton(VaadinIcon.FAST_FORWARD.create(), e -> handleClick(e));
				next = new VButton(VaadinIcon.FORWARD.create(), e -> handleClick(e));
				previous = new VButton(VaadinIcon.BACKWARDS.create(), e -> handleClick(e));
			}

			private void handleClick(ClickEvent<Button> event) {
				if (event.getSource() == first) {
					currentPage = 0;
				} else if (event.getSource() == last) {
					currentPage = pages - 1;
				} else if (event.getSource() == next) {
					currentPage++;
				} else if (event.getSource() == previous) {
					currentPage--;
				}
				List<T> page = dataProvider.pageRequested(currentPage);
				if (page.size() > 0) {
					setItems(page);
					if (page.size() < getPageSize()) {
						setSize(page.size() + currentPage * getPageSize());
					}
				} else {
					setSize(currentPage * getPageSize());
					if (currentPage > 0) {
						currentPage--;
					}
				}
				updateState();
			}

			private Long size;
			private long currentPage;
			private Long pages;

			public long getSize() {
				return size;
			}

			private Button first, last, next, previous;
			private final Span status = new Span();

			public PaginationBar(Long size) {
				setSize(size);
				initButtons();
				updateState();
				add(first, previous, status, next, last);
				alignAll(Alignment.CENTER);
				withFullWidth();
			}

			void setSize(Long s) {
				this.size = s;
				if (sizeKnown()) {
					pages = (size / getPageSize());
				}
			}

			private boolean sizeKnown() {
				return size != null;
			}

		}

		public void setTotalResults(long totalResults) {
			paginationBar.setSize(totalResults);
			paginationBar.updateState();
		}

	}

	public TraditionalPaging() {
		final PagingGrid<Person> table = new PagingGrid<>(Person.class);

		// Define results with a simpler data provider API, that just gives you page to
		// request
		table.setPagingDataProvider(page -> {
			// This is demo specific line, normally, e.g. with spring data, page number is
			// enough
			int start = (int) (page * table.getPageSize());
			// Optional, sorting
			if (!table.getSortOrder().isEmpty()) {
				GridSortOrder<Person> sortOrder = table.getSortOrder().get(0);
				String propertyId = sortOrder.getSorted().getKey();
				boolean asc = sortOrder.getDirection() == SortDirection.ASCENDING;
				return Service.findAll(start, table.getPageSize(), propertyId, asc);
			}
			return Service.findAll(start, table.getPageSize());
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

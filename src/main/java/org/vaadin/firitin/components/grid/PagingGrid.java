package org.vaadin.firitin.components.grid;

import java.util.List;

import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.event.SortEvent;

/**
 * A Grid which uses "traditional" paging, instead of the lazy loading while
 * scrolling commonly used in Vaadin apps.
 * 
 * @author mstahv
 *
 * @param <T> the the of row shown in the grid
 */
public class PagingGrid<T> extends VGrid<T> {

	public interface PagingDataProvider<T> {
		
		/**
		 * Returns one page from the database. 
		 * @param page the page number
		 * @param pageSize the number of results on a page
		 * @return the result list
		 */
		List<T> pageRequested(long page, int pageSize);
	}

	private PagingGrid.PagingDataProvider<T> dataProvider;
	private PagingGrid<T>.PaginationBar paginationBar;
	private ComponentEventListener<SortEvent<Grid<T>, GridSortOrder<T>>> sortListener = event -> {
		// scroll to the beginning and fetch new rows (up to the implementation to read
		// sort order from the grid)
		setItems(dataProvider.pageRequested(paginationBar.currentPage, getPageSize()));
	};

	public PagingGrid() {
		super();
		init();
	}

	private void init() {
		addSortListener(this.sortListener);
		setHeightByRows(true);
		setPageSize(10);
	}

	public PagingGrid(Class<T> beanType) {
		super(beanType);
		init();
	}

	public PagingGrid(int pageSize) {
		super(pageSize);
		init();
	}

	private void preparePaginationBar() {
		paginationBar = new PaginationBar(null);
		HeaderRow headerRow = prependHeaderRow();
		headerRow.join(getColumns().toArray(new Column[0])).setComponent(paginationBar);
	}

	public PagingGrid.PagingDataProvider<T> getPagingDataProvider() {
		return dataProvider;
	}

	/**
	 * Defines the way how data is fetched from the backend. To define the page length, use {@link #setPageSize(int)} method.
	 * 
	 * @param provider the data provider that fetches data from the backend by pages.
	 */
	public void setPagingDataProvider(PagingGrid.PagingDataProvider<T> provider) {
		this.dataProvider = provider;
		preparePaginationBar();
		setItems(dataProvider.pageRequested(0, getPageSize()));
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
			List<T> page = dataProvider.pageRequested(currentPage, getPageSize());
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

	/**
	 * This method can optionally be used to define the size of the whole data set
	 * on all pages. If size is defined, the pagination bar shows "jump to last
	 * page" button and the status bar can report how many pages of results there
	 * are in total.
	 * 
	 * @param totalResults the amount of results
	 */
	public void setTotalResults(long totalResults) {
		paginationBar.setSize(totalResults);
		paginationBar.updateState();
	}

}
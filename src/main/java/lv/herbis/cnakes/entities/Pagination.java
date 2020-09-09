package lv.herbis.cnakes.entities;

public class Pagination {
	private int page = 1;
	private int pageCount;

	public Pagination(final int pageCount) {
		this.pageCount = pageCount;
	}

	public boolean nextPage() {
		return setPage(getPage() + 1);
	}

	public boolean previousPage() {
		return setPage(getPage() - 1);
	}

	public int getPage() {
		return this.page;
	}

	public int getPageCount() {
		return pageCount;
	}

	private boolean setPage(final int page) {

		/* Make sure invalid page not set. */
		if (page < 1 || page > this.pageCount) {
			return false;
		}

		this.page = page;
		return true;
	}
}

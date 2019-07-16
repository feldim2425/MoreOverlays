package at.feldim2425.moreoverlays.api.itemsearch;

public class SlotViewWrapper {
	private boolean enableOverlay = false;
	private final IViewSlot view;

	public SlotViewWrapper(IViewSlot view){
		this.view = view;
	}

	public boolean isEnableOverlay() {
		return enableOverlay;
	}

	public void setEnableOverlay(boolean enableOverlay) {
		this.enableOverlay = enableOverlay;
	}

	public IViewSlot getView() {
		return view;
	}
}

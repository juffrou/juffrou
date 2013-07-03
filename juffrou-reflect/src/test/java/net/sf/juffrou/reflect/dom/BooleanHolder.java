package net.sf.juffrou.reflect.dom;

public class BooleanHolder {

	private boolean isDirty;
	private boolean hasContent;
	private Boolean isClean;
	private Boolean hasCash;
	
	
	public boolean isDirty() {
		return isDirty;
	}
	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}
	public boolean isHasContent() {
		return hasContent;
	}
	public void setHasContent(boolean hasContent) {
		this.hasContent = hasContent;
	}
	public Boolean getIsClean() {
		return isClean;
	}
	public void setIsClean(Boolean isClean) {
		this.isClean = isClean;
	}
	public Boolean getHasCash() {
		return hasCash;
	}
	public void setHasCash(Boolean hasCash) {
		this.hasCash = hasCash;
	}
	
	
}

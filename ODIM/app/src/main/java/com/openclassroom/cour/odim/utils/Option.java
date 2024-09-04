package com.openclassroom.cour.odim.utils;

public class Option implements Comparable<Option>{
	private final String name;
	private final String data;
	private final String path;
	private final boolean folder;
	private final boolean parent;
	
	public Option(String n,String d,String p, boolean folder, boolean parent)
	{
		name = n;
		data = d;
		path = p;
		this.folder = folder;
		this.parent = parent;
	}
	public String getName()
	{
		return name;
	}
	public String getData()
	{
		return data;
	}
	public String getPath()
	{
		return path;
	}
	@Override
	public int compareTo(Option o) {
		if(this.name != null)
			return this.name.toLowerCase().compareTo(o.getName().toLowerCase()); 
		else 
			throw new IllegalArgumentException();
	}
	public boolean isFolder() {
		return folder;
	}
	public boolean isParent() {
		return parent;
	}
}

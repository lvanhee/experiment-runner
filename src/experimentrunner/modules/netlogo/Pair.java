package experimentrunner.modules.netlogo;

public class Pair<T> {
	
	private final T left;
	private final T right;
	
	public Pair(T l, T r)
	{
		this.left = l;
		this.right = r;
	}

	public T left() {
		return left;
	}

	public T right() {
		return right;
	}
	
	public String toString()
	{
		return left+","+right;
	}

}

import java.util.ArrayList;import java.util.Collection;import java.util.List;import android.os.Handler;import android.os.Looper;import android.support.annotation.NonNull;import android.view.View;import android.view.ViewGroup;import android.widget.BaseAdapter;/** * <pre> * 千万不要重载{@link LIBBaseAdapter#getView(int, View, ViewGroup)} * </pre> *  * @Description <通用的Adapter> * @author lixi * @date 2015-5-19 */public abstract class LIBBaseAdapter<T> extends BaseAdapter implements OnDatasetChangeListener {	protected List<T> mData;	private Handler uiHandler;	protected synchronized final Handler getHandler() {		if (uiHandler == null) {			uiHandler = new Handler(Looper.getMainLooper());		}		return uiHandler;	}	public LIBBaseAdapter() {		this(null);	}	public LIBBaseAdapter(List<T> list) {		if (list == null) {			mData = new ArrayList<T>();		} else {			mData = list;		}		init();	}	protected void init() {	}	/** 直接使用外部的List,自动调用notifyDataSetChanged */	public void setData(@NonNull List<T> list) {		mData = list;		notifyDataSetChanged();	}	public List<T> getList() {		return mData;	}	/** 添加数据 ,自动调用notifyDataSetChanged */	public boolean addData(Collection<? extends T> collection) {		return addData(collection, false);	}	/** 如果是第一笔数据，则会清空之前数据,自动调用notifyDataSetChanged */	public boolean addData(Collection<? extends T> collection, boolean isFirstData) {		if (null == collection || collection.isEmpty()) {			return false;		}		if (isFirstData) {			mData.clear();		}		mData.addAll(collection);		notifyDataSetChanged();		return true;	}	/** 添加1个数据,自动调用notifyDataSetChanged */	public void addData(T data) {		mData.add(data);		notifyDataSetChanged();	}	/** 移除1个数据,自动调用notifyDataSetChanged */	public T remove(int index) {		T t = this.mData.remove(index);		notifyDataSetChanged();		return t;	}	/** 移除1个数据,外部调用notifyDataSetChanged,需重载 {@link Object#equals(Object)} */	public T remove(Object object) {		if (null == object) {			return null;		}		for (int i = 0, size = null == mData ? 0 : mData.size(); i < size; i++) {			T t = mData.get(i);			if (null != t && t.equals(object)) {				return mData.remove(i);			}		}		return null;	}	/** 添加数据,自动调用notifyDataSetChanged */	public void addData(int position, T data) {		mData.add(position, data);		notifyDataSetChanged();	}	/** 添加数据,自动调用notifyDataSetChanged */	public boolean addData(int position, Collection<? extends T> collection) {		if (null == collection || collection.isEmpty()) {			return false;		}		mData.addAll(position, collection);		notifyDataSetChanged();		return true;	}	/** 清空数据,自动调用notifyDataSetChanged */	public void clear() {		mData.clear();		notifyDataSetChanged();	}	@Override	public int getCount() {		return mData.size();	}	public int getSize() {		return mData.size();	}	@Override	public T getItem(int position) {		return mData.get(position);	}	/** 获得某个item,需重载 {@link Object#equals(Object)} */	public T getItem(Object object) {		if (object == null) {			return null;		}		for (int i = 0, size = null != mData ? mData.size() : 0; i < size; i++) {			T t = getItem(i);			if (null != t && t.equals(object)) {				return t;			}		}		return null;	}	public T getLastItem() {		return mData.get(mData.size() - 1);	}	@Override	public long getItemId(int position) {		return position;	}	private ArrayList<ViewHolder> holders = new ArrayList<ViewHolder>();	private ViewHolder getHolder(Object data) {		if (null == data) {			return null;		}		for (int i = 0, size = holders.size(); i < size; i++) {			ViewHolder holder = holders.get(i);			if (holder.equals(data)) {				return holder;			}		}		return null;	}	@Override	public final void onChange(ViewHolder holder) {		T data = holder.getTag();		holder.markRecyclerHolder();// 标记当前是重用状态		convert(holder, data, holder.getItemViewType(), holder.getPosition());// 转换布局		holder.resetRecyclerHolder();// 重置标记	}	@Override	public final synchronized void notifyDataSetChanged() {		if (Looper.myLooper() != Looper.getMainLooper()) {// 如果不是主线程，就交给Handler做处理			getHandler().post(new Runnable() {				@Override				public void run() {					LIBBaseAdapter.super.notifyDataSetChanged();				}			});			return;		} else {			super.notifyDataSetChanged();		}	}	/** 刷新Item,需重写 {@link Object#equals(Object)} */	public final synchronized void refreshItem(final Object item) {		if (Looper.myLooper() != Looper.getMainLooper()) {// 如果不是主线程，就交给Handler做处理			getHandler().post(new Runnable() {				@Override				public void run() {					refreshItem(item);				}			});			return;		}		ViewHolder holder = getHolder(item);// 查找Holder,根据Holder缓存的Tag和当前传入的item匹配		if (null == holder) {// 如果找不到Holder，则刷新全部			notifyDataSetChanged();			return;		} else {			onChange(holder);			return;		}	}	@Deprecated	@Override	public View getView(int position, View convertView, ViewGroup parent) {		T data = getItem(position);		int itemViewType = getItemViewType(position);		ViewHolder holder = ViewHolder.getHolder(convertView, parent, getLayoutId(data, itemViewType, position), position, itemViewType);		holder.setTag(data);		if (!holder.isRecyleHolder()) {			holders.add(holder);// 缓存Holder			holder.setListener(this);// 设置刷新监听		}		convert(holder, data, itemViewType, position);// 回调UI		return holder.getConvertView();// 返回主布局,即ConvertView	}	/** 转换布局 */	public abstract void convert(ViewHolder holder, T data, int itemViewType, int position);	/** 获取布局id */	public abstract int getLayoutId(T data, int itemViewType, int position);}
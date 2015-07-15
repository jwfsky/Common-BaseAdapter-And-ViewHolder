#序
	   自从看了鸿洋[^footnote]的[Android-打造万能的适配器](http://www.imooc.com/view/372)
	   后一直想要根据自己的需求进行拓展一个出来

[TOC]


##LIBBaseAdapter 

###前言
	在定义LIBBaseAdapter的时候，首先考虑通用性，以及平常使用过程中，迫切的便利需求，所以在拓展的LIBBaseAdapter中提供了一个UIHandler，如果当前的Looper不是MainLooper的时候，通过UIHandler来更新UI。

###特性
- 抽取BaseAdapter通用部分代码
-  通过泛型，抽出List（并支持外部指定），并在Adapter中提供Add，Remove操作
- 支持通过Object类型查找数据，比如通过某个id来查找list，获得对item的操作。**（需重写Data的equals（））**
- ViewHolder缓存，仅缓存可见Holder，不会重复缓存
- **支持刷新单个item**
- **不支持自定义布局**

###使用示例
```java
final LIBBaseAdapter<DATA> adapter = new LIBBaseAdapter<DATA>(){
	@Override
		public int getLayoutId(DATA data, int itemViewType, int position) {
			return position == 0 ? R.layout.xx : R.layout.xx;
		}

		public int getItemViewType(int position) {
			return position == 0 ? 0 : 1;
		};

		public int getViewTypeCount() {
			return 2;
		};

		@Override
		public void convert(ViewHolder holder, DATA data, int itemViewType, int position) {
			if (position == 0) {
				holder.setText(R.id.xx, xx);
				holder.setVisibility(R.id.container, View.VISIBLE);
				holder.setVisibility(R.id.tv_empty, View.GONE);
			}
			holder.loadImage(R.id.xx, data.getUsericon(), R.drawable.xx);
			
			TextView nooiceTv = holder.setText(R.id.xx, data.getNooiceNum());
			nooiceTv.setTag(data);
			nooiceTv.setSelected(data.isNooice());
			if (!holder.isRecyleHolder()) {
				nooiceTv.setOnClickListener(clickListener);
			}
		}
}
```

##ViewHolder
###前言
	同于LIBBaseAdaper，在以往中定义ViewHolder的时候，往往会有多种考虑，不同的布局需要重复的去写一大堆的代码，因此即于视频之后，自定义了一个属于自己的Holder，当然你也可以根据你的需求来写一个自己的ViewHolder

###特性
- **使用SparseArray对View进行缓存，每次getView操作都优先使用缓存，若无缓存才从父布局查找。**
- **通过泛型返回所需的View，不便之处：[姨妈出血][1]**
- 支持刷新单个item，需配合LIBBaseAdapter使用（当然也可拓展到自定义View）
- 缓存LIBBaseAdapter中提供的position，DATA，itemViewType，layoutId
- **非与LIBBaseAdapter搭配使用时，和Adapter相关方法失效**

###使用示例
```java
ViewHolder holder = ViewHolder.getHolder(this, parentView,R.layout.xx, true);
		holder.loadImage(R.id.xx, scope.getIconUrl(), R.drawable.xx);
		holder.setText(R.id.tv_title, scope.getTitle());
```

###未来
-提供给自定义的View使用

#About me
[黎稀](http://www.cxh.name/)
[CSDN](http://blog.csdn.net/darryl0912)
#Contact me
lixi0912@gmail.com

#License
```
Copyright (c) 2015 lixi

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
Come on, don't tell me you read that.
```



[^footnote]:[鸿洋 CSDN 传送门](http://blog.csdn.net/lmj623565791)

[1]:ClassCastException

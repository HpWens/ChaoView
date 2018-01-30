# ChaoView
### 一、前言
一直都想跟大家分享一篇好的文章，但苦于心中的墨水，写出来都平平无奇。我自己也在反思是不是没有表达清楚了，本篇文章我会尽量详细的讲解。
好久没有文章更新了，是我欠大家的一份承诺，拖拉的习惯一直没有改掉，希望大家能够监督监督我，同时我也尽量克制自己的懒惰，及时分享自己的一些学习成果。
### 二、正文
习惯了先上效果图，再逐一解剖，图文并茂有助于大家的理解
#### 1、效果图
![demo.gif](http://upload-images.jianshu.io/upload_images/2258857-ba6edb8cab3eb41f.gif?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
录制的不是很清晰，文章的末尾会给出源码地址。
第一眼看到这个效果的时候我就想到了通过自定义View去实现（最终还真被我实现了七七八八的效果），简单的分析下自定义控件的基本需求有以下几种：

 1. 整个屏幕作为把手（可以拖拽的）进行拖拽
 2. 底部上拉布局有一定的高度限制，不一定覆盖整个屏幕（高度根据具体情况调整）
 3.当从底部上拉一点点时抬手，布局缩回，若超过一定高度，自动展开到最大，下拉同理（根据具体情况设定阈值） 
 4. 支持快速拖拽（效果同3）
 5. 根据布局滑动的偏移量，来控制其他 View 的动画效果
 6. 若 ViewPager 嵌套 Fragment + 滚动 View ，需要处理滑动冲突（若 [滚动View] 滑动到了顶部并且下滑的趋势大于左右的趋势，则让他父类拦截事件，反之则自己消费事件）
 
通过自定义控件的方式很难实现运动惯性的效果，总感觉有瑕疵，那么只能另寻方案。苦思冥想... 滑动...折叠...好像抓住了什么，对，就是折叠效果，一下让我想到了 [design](https://guides.codepath.com/android/Design-Support-Library)库下面的 CoordinatorLayout 视图，我相信大家对它并不陌生，是 Google IO/15 大会发布的，专门用来打造各种炫酷的效果，一般是结合 AppbarLayout, CollapsingToolbarLayout, Toolbar 来使用。如果你对 CoordinatorLayout 的使用还不是很熟悉，推荐浏览以下地址：

[CoordinatorLayout 英文地址](https://guides.codepath.com/android/Handling-Scrolls-with-CoordinatorLayout)

[CoordinatorLayout 中文地址（任玉刚）](http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2016/0224/3991.html)

网上有关 CoordinatorLayout，AppBarLayout 的文章太多了，我这里就不再赘述。

#### 2、CoordinatorLayout的xml布局

![xml](http://img.blog.csdn.net/20180130001659713?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdTAxMjU1MTM1MA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

由于 xml 布局文件太长，查看布局请点击文章末尾的源码链接地址。

AppBarLayout（通过手势变化来控制子View的运动轨迹）是 CoordinatorLayout的第一个子View（孩子）

    <android.support.design.widget.CollapsingToolbarLayout
        android:id="@+id/collapsing"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:collapsedTitleGravity="left|top|start"
        app:layout_scrollFlags="scroll|exitUntilCollapsed|snap"
        app:layout_scrollInterpolator="@android:anim/linear_interpolator">
		
app:layout_scrollFlags="scroll|exitUntilCollapsed|snap"	![layout_scrollFlags 的相关介绍](https://www.jianshu.com/p/7caa5f4f49bd) snap 属性控制手指抬起后Child View要么向上全部滚进屏幕，要么向下全部滚出屏幕
	
	<ImageView
                android:id="@+id/iv_bg"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:scaleType="centerCrop"
                android:src="@mipmap/ic_bg"
                app:layout_collapseMode="parallax"
                app:layout_collapseParallaxMultiplier="1.0"/>
				
CollapsingToolbarLayout 的 Child View 都设置了以下属性： 	
	
	app:layout_collapseMode="parallax"
    app:layout_collapseParallaxMultiplier="1.0"		
	
parallax 滚动有视差效果，layout_collapseParallaxMultiplier="1.0" 相当于你滚动了多少，我就滚动多少。
	
	    <android.support.design.widget.TabLayout
        android:layout_height="@dimen/dp_4"
        android:id="@+id/tab_layout"
        android:layout_width="match_parent"
        android:layout_alignParentBottom="true"
        android:layout_alignParentRight="true"
        android:layout_centerHorizontal="true"
        android:layout_marginLeft="@dimen/dp_8"
        android:layout_marginRight="@dimen/dp_8"
        app:tabGravity="fill"
        app:tabIndicatorColor="@color/color_ff0000"
        app:tabIndicatorHeight="@dimen/dp_4"
        app:tabMinWidth="16dp"/>
		
TabLayout + ViewPager 实现多个界面的切换  android:layout_height="4dp" 刚好显示的指示器的高度，刚开始把高度设置成 56dp ，发现与 [作品] 区域并不好交互。所以这里有个讨巧的办法TabLayout只显示指示器的高度，并监听mTabLayout.addOnTabSelectedListener 来实现指示器的动画效果。
	
布局就讲到这里了，下面来看一下运行效果：
	
![scroll](http://img.blog.csdn.net/20180130002124308?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdTAxMjU1MTM1MA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

这里有一点需要注意的地方，ViewPager的Fragment里的滚动视图必须继承于NestedScrollingChild、NestedScrollingParent，不然滚动到最顶部 AppBarLayout 不会有下拉效果 。

接下来我以头像+作品区域的运动效果来简单介绍下。

#### 3、头像运动效果
拆分运动效果：

 1. 平移动画  X轴方向运动到左上角；Y轴方向也运动到左上角
 2. 缩放动画 X轴方向缩放到0.5；Y轴方向也缩放到0.5（这里是原点缩放，默认的是中心点缩放）

怎么来监听滚动事件呢，需要实现 AppBarLayout 运动偏移量的接口：

```
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

    }
```

参数 verticalOffset 垂直方向的偏移量

```
appBarLayout.getTotalScrollRange() 
```

表示总共可以滑动的范围也可以理解成最大的偏移量

那么我们就可以拿到滑动比率 ：

```
float ratio = Math.abs((float) verticalOffset / appBarLayout.getTotalScrollRange()); //[0~1]
```

头像的缩放范围是 [1~0.5] ，那么我们可以进行如下处理：

```
    mHeaderView.setPivotX(0);
    mHeaderView.setPivotY(0);
    mHeaderView.setScaleY(0.5f + 0.5f * (1.0f - ratio));
    mHeaderView.setScaleX(0.5f + 0.5f * (1.0f - ratio));
```

 头像的平移动画也拆分两步
 
 1. 头像保持不动
 2. 在1的前提下进行平移

由于 CollapsingToolbarLayout 设置的子 View 的 layout_collapseParallaxMultiplier 的视差参数是1.0，那么视图向上移动的距离就是verticalOffset 垂直方向上的偏移量，由于向上移动Y坐标在减小，为了保持位置不动就加上垂直方向的偏移量。

```
mHeaderView.setY(mHeaderStartY + Math.abs(verticalOffset));
```
mHeaderStartY 变量表示头像运动前的Y坐标。这样就可以使头像的位置固定不变，接下来在固定的基础上进行平移

```
   mHeaderView.setY(mHeaderStartY + Math.abs(verticalOffset) - (mHeaderStartY - mHeaderEndY) * ratio);
   
   mHeaderView.setX(mHeaderStartX - (mHeaderStartX - mHeaderEndX) * ratio);
```

mHeaderStartX表示头像运动前的X坐标，mHeaderEndX，mHeaderEndY 分别是左上角的坐标分别为18dp，12dp

这样头像的运动效果就实现了，接下来看看 作品区域的动画效果



其他的效果类似。这里就不在赘述。

其他效果完成之后，运行走一波。玩着玩着就发现，为啥 RecyclerView 未滚动到顶部，下拉 [头像+作品] 区域，AppBarLayout 并不会向下滚动。但潮自拍的个人中心却可以滚动。追求卓越品质的我，怎么会允许这样的问题存在呢。

先搜搜发现网上没人遇到过，那么只能看 CoordinatorLayout  AppBarLayout 源码了，断点调试了 CoordinatorLayout onTouchEvent 方法发现并没有找到突破口。 苦思冥想 ... 冥想苦思 ...

突然想到了关联的滚动视图必须继承 NestedScrollingChild、NestedScrollingParent 接口，那么是搜索他两试试呢？一搜索还真被我找到了，当时内心的那份喜悦是无以言表的。

在 AppBarLayout 类里面搜索 NestedScrollingChild 

```
private WeakReference<View> mLastNestedScrollingChildRef;
```

发现了一个加有弱引用的 View，继续跟踪（省略了初始化的地方），就定位到了下面这个方法：

```
        @Override
        boolean canDragView(AppBarLayout view) {
            if (mOnDragCallback != null) {
                // If there is a drag callback set, it's in control
                return mOnDragCallback.canDrag(view);
            }

            // Else we'll use the default behaviour of seeing if it can scroll down
            if (mLastNestedScrollingChildRef != null) {
                // If we have a reference to a scrolling view, check it
                final View scrollingView = mLastNestedScrollingChildRef.get();
                return scrollingView != null && scrollingView.isShown()
                        && !ViewCompat.canScrollVertically(scrollingView, -1);
            } else {
                // Otherwise we assume that the scrolling view hasn't been scrolled and can drag.
                return true;
            }
        }
```

代码很简单，重点看

```
ViewCompat.canScrollVertically(scrollingView, -1);
```

滑到最顶部时，返回 false 。那么 canDragView 就很清晰了，滚动 View 未滑动到顶部返回 false ；滑动到顶部则放回 true 

继续跟踪 canDragView 在哪里被调用了 

HeaderBehavior 类下的 onTouchEvent 方法 ：

```
    case MotionEvent.ACTION_DOWN: {
        final int x = (int) ev.getX();
        final int y = (int) ev.getY();
        if (parent.isPointInChildBounds(child, x, y) && canDragView(child)) {
           //滚动到顶部  下拉执行这里
            mLastMotionY = y;
            mActivePointerId = ev.getPointerId(0);
            ensureVelocityTracker();
        } else {
        // 未滚动到顶部 下拉执行这里
            return false;
        }
        break;
    }
```
并且我调试跟踪 RecyclerView 滚动到顶部与未滚动到顶部下拉 [头像+作品] 区域，断点刚好触发在这里，请看注释。源码就讲到这里，再讲解可能会绕晕。

相信大家看到这里，解决的思路都非常明了了。

重写 RecyclerView 的 canScrollVertically 方法返回 false

```
    @Override
    public boolean canScrollVertically(int direction) {
        return false;
    }
```

本篇到这里就要结束了，下一篇带个大家实现知乎列表图片的滚动视差效果。


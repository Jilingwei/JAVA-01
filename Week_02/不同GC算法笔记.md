## 不同GC算法笔记

## 标记清除算法

1. 算法介绍

   标记清除法是现在GC算法的基础，目前似乎没有哪个GC还在使用这种算法了。因为这种算法会产生大量的内存碎片。

   标记清除算法的执行过程分为两个阶段：标记阶段、清除阶段。

   - 标记阶段会通过可达性分析将不可达的对象标记出来。
   - 清除阶段会将标记阶段标记的垃圾对象清除。



![image-20210123210658930](https://tva1.sinaimg.cn/large/008eGmZEgy1gmxxmig6z7j30fp0ac74m.jpg)



2. 算法特点

   该算法存在明显的缺点，回收后会产生大量不连续的内存空间，即内存碎片。由于Java在分配内存时通常是按连续内存分配，那么当碎片空间不足以分配给新的对象时，就造成了内存浪费。

## 复制算法

1. 算法介绍

   复制算法会将内存空间分为两块，每次只使用其中一块内存。复制算法同样使用可达性分析法标记除垃圾对象，当GC执行时，会将非垃圾对象复制到另一块内存空间中，并且保证内存上的连续性，然后直接清空之前使用的内存空间。然后如此往复。

   我们姑且将这两块内存区域称为from区和to区。

   如下图所示，r1和r2作为GC Root对象，经过可达性分析后，标记除黄色对象为垃圾对象。

   

   ![img](https://user-gold-cdn.xitu.io/2019/1/18/1686137101be3578?imageslim)

   

   复制过程如下，GC会将五个存活对象复制到to区，并且保证在to区内存空间上的连续性。

   ![img](https://user-gold-cdn.xitu.io/2019/1/18/168613745047ce7d?imageslim)

   最后，将from区中的垃圾对象清除。

   ![img](https://user-gold-cdn.xitu.io/2019/1/18/16861376a249d525?imageslim)

   2. 算法特点

      该算法在存货对象少，垃圾对象多的情况下，非常高效。其好处是不会产生内存碎片，但坏处也是显而易见的，就是直接损失了一半的可用内存。

   ## 标记压缩法

   1. 算法介绍

      标记压缩法主要分成三个步骤

      + 标记垃圾对象
      + 清除垃圾对象
      + 内存碎片处理

      过程如下：

      首先标记除垃圾对象（黄色）

      ![img](https://user-gold-cdn.xitu.io/2019/1/18/1686137e796cb5c9?imageslim)

      清除垃圾对象

      ![img](https://user-gold-cdn.xitu.io/2019/1/18/1686139f6a37c8fc?imageslim)

      内存碎片整理

      ![img](https://user-gold-cdn.xitu.io/2019/1/18/168613a4abc51785?imageslim)

      2. 算法特点

         该算法的特点在于，内存的使用率比复制发更高，但是需要花时间再整理内存碎片上。

      ## 分代算法

      分代算法基于复制算法和标记压缩算法。

      首先，标记清除算法、复制算法、标记压缩算法都有各自的缺点，如果单独用其中某一算法来做GC，会有很大的问题。

      例如，标记清除算法会产生大量的内存碎片，复制算法会损失一半的内存，标记压缩算法的碎片整理会造成较大的消耗。

      其次，复制算法和标记压缩算法都有各自适合的使用场景。

      复制算法适用于每次回收时，存活对象少的场景，这样就会减少复制量。

      标记压缩算法适用于回收时，存活对象多的场景，这样就会减少内存碎片的产生，碎片整理的代价就会小很多。

      分代算法将内存区域分为两部分：新生代和老年代。

      根据新生代和老年代中对象的不同特点，使用不同的GC算法。

      新生代对象的特点是：创建出来没多久就可以被回收（例如虚拟机栈中创建的对象，方法出栈就会销毁）。也就是说，每次回收时，大部分是垃圾对象，所以新生代适用于复制算法。

      老年代的特点是：经过多次GC，依然存活。也就是说，每次GC时，大部分是存活对象，所以老年代适用于标记压缩算法。

      新生代分为eden区、from区、to区，老年代是一整块内存空间，如下所示：

      ![img](https://tva1.sinaimg.cn/large/008eGmZEgy1gmxxvizhu7j30nc0kgdg4.jpg)

      

      **分代算法执行过程**

      

      首先简述一下新生代GC的整个过程（老年代GC会在下面介绍）：新创建的对象总是在eden区中出生，当eden区满时，会触发Minor GC，此时会将eden区中的存活对象复制到from和to中一个没有被使用的空间中，假设是to区（正在被使用的from区中的存活对象也会被复制到to区中）。

      有几种情况，对象会晋升到老年代：

      - 超大对象会直接进入到老年代（受虚拟机参数-XX:PretenureSizeThreshold参数影响，默认值0，即不开启，单位为Byte，例如：3145728=3M，那么超过3M的对象，会直接晋升老年代）
      - 如果to区已满，多出来的对象也会直接晋升老年代
      - 复制15次(15岁)后，依然存活的对象，也会进入老年代

      此时eden区和from区都是垃圾对象，可以直接清除。

      PS：为什么复制15次(15岁)后，被判定为高龄对象，晋升到老年代呢？

      因为每个对象的年龄是存在对象头中的，对象头用4bit存储了这个年龄数，而4bit最大可以表示十进制的15，所以是15岁。

      下面从JVM启动开始，描述GC的过程。

      JVM刚启动并初始化完成后，几块内存空间分配完毕，此时状态如上图所示。

      （1）新创建的对象总是会出生在eden区

      <img src="https://tva1.sinaimg.cn/large/008eGmZEgy1gmxxvv3wtyj30ni0km3yw.jpg" alt="img" style="zoom:67%;" />

      （2）当eden区满的时候，会触发一次Minor GC，此时会从from和to区中找一个没有使用的空间，将eden区中还存活的对象复制过去（第一次from和to都是空的，使用from区），被复制的对象的年龄会+1，并清除eden区中的垃圾对象。

      <img src="https://tva1.sinaimg.cn/large/008eGmZEgy1gmxxw89rwnj30nj0kg3zp.jpg" alt="img" style="zoom:67%;" />

      （3）程序继续运行，又在eden区产生了新的对象，并产生了一个超大对象，并产生了一个复制后to区放不下的对象

      <img src="https://tva1.sinaimg.cn/large/008eGmZEgy1gmxxwkukotj30ne0kadg9.jpg" alt="img" style="zoom:67%;" />

      （4）当eden区再次被填满时，会再一次触发Minor GC，这次GC会将eden区和from区中存活的对象复制到to区，并且对象年龄+1，超大对象会直接晋升到老年代，to区放不下的对象也会直接晋升老年代。

      <img src="https://tva1.sinaimg.cn/large/008eGmZEgy1gmxxwvkglbj30ne0kg764.jpg" alt="img" style="zoom:67%;" />

      （5）程序继续运行，假设经过15次复制，某一对象依然存活，那么他将直接进入老年代。

      <img src="https://tva1.sinaimg.cn/large/008eGmZEgy1gmxxxbhqkmj30ne0k9myx.jpg" alt="img" style="zoom:67%;" />

      （6）老年代 Full GC

      

      在进行Minor GC之前，JVM还有一步操作，他会检查新生代所有对象使用的总内存是否小于老年代最大剩余连续内存，如果上述条件成立，那么这次Minor GC一定是安全的，因为即使所有新生代对象都进入老年代，老年代也不会内存溢出。如果上述条件不成立，JVM会查看参数HandlePromotionFailure[1]是否开启（JDK1.6以后默认开启），如果没开启，说明Minor GC后可能会存在老年代内存溢出的风险，会进行一次Full GC，如果开启，JVM还会检查历次晋升老年代对象的平均大小是否小于老年代最大连续内存空间，如果成立，会尝试直接进行Minor GC，如果不成立，老年代执行Full GC。

      <img src="https://tva1.sinaimg.cn/large/008eGmZEgy1gmxy1ba1hej30nl0gidi0.jpg" alt="img" style="zoom:67%;" />

      eden区和from区的存活对象会复制到to区，超大对象和to区容纳不下的对象会直接晋升老年代。当eden区满时，触发Minor GC，此时判断老年代剩余连续内存已经小于新生代所有对象占用内存总和，假设HandlePromotionFailure参数开启，JVM还会继续判断老年代剩余连续内存是否大于历次晋升老年代对象的平均大小，如图所示，目前老年代还剩2个空间，如果之前平均每次晋升三个对象到老年代，剩余空间小于平均值，会触发Full GC。
   
      
      
      老年代回收-标记：
      
      <img src="https://tva1.sinaimg.cn/large/008eGmZEgy1gmxxzdyraqj30ne06y3yh.jpg" alt="img" style="zoom:67%;" />
      
      老年代回收-清除：
      
      <img src="https://tva1.sinaimg.cn/large/008eGmZEgy1gmxxzq66ybj30ne06pmx4.jpg" alt="img" style="zoom:67%;" />
      
      老年代回收-碎片整理：
      
      <img src="https://tva1.sinaimg.cn/large/008eGmZEgy1gmxxzxktj1j30nj06wq2w.jpg" alt="img" style="zoom:67%;" />
      
      
   
   
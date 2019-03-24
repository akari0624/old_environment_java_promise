### 舊java環境中，有用的java模組程式 by MorrisC
- 這些模組包括：
  1. 將`Guava 11`的ListenableFuture與JAVA5的ExecutorService再包裝起來而形成的 執行緒池與非同步模組
     - com.morrisc.concurrrency.async_work_service

### 注意事項     
- 本project想要讓還在使用`JAVA 5 ~ 7`，因為種種原因無法升級到`JAVA 8`以上的老舊執行環境也能夠簡單地使用好用的`類似JS的Promise`,`JAVA 8的CompletableFuture`那樣的非同步,多執行緒操作的功能。  
- 如果您能使用`JAVA 8`以上，千萬不要使用這個Library,因為有更好用更強大的[CompletableFuture](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html)可用
- 這是一個`gradle`的JAVA project，要參與開發或是編譯出jar檔的人的開發電腦上必須安裝`Gradle 4.10.2`版本。 [link](https://gradle.org/releases/)
- 不確定`Gradle 5`還有沒有支援`JAVA 5`，所以要編譯本library的話`不建議`使用Gradle 5
- 本模組是將`Guava`的[ListenableFuture](https://google.github.io/guava/releases/23.0/api/docs/com/google/common/util/concurrent/ListenableFuture.html)再做一層包裝而成，所以依賴`Guava`如果要跑在`JAVA 5`的執行環境上，`Guava`一定要使用`11.0.2`，那是Guava還支援JAVA 5的最後一版。


  
### 使用情境
1. 
  - 適合同時要做多個網路IO，然後在`request`都發出去之後，最後在一個地方`blocking`住，然後最後一次取得`所有`這些request的response的場合。
  - 不然一個發完收到回應後再發另一個，等它回應，然後再發第三個.....這樣會很慢。


### 使用方式
``` java

List<ListenableFuture<Integer>> fList = new ArrayList<ListenableFuture<Integer>>();

ListenableFuture<Integer> aFuture = AsyncWorkService.getInstance().sendWorkToBackgroundExecutorService(
   new InitDelegateWorker<R,T>(){
      
      // ....在這裡面發生的所有事情，都是在非主執行緒裡執行 
      @Override
      public T doInBackground(T initParameterForDoinBackGround) throws Exception
      // initParameterForDoinBackGround 會備傳進這裡，

      @Override
		public Integer onBackgroundWorkSuccess(T result)
      // 當doInBackground 裡執行成功後, doInBackground那裡return的value就是它的result參數

      @Override
		public void onBackgroundWorkError(Throwable throwable)
      // 發生Exception時做怎樣的事，通常是log錯誤訊息，

   }, defaultReturnValueWhenTaskFail:R, initParameterForDoinBackGround:T  )

```
###  範例：
1. 執行40個任務，放進執行緒池裡並行(concurrently)執行，在最後等所有任務執行完，當中可能有些任務會是失敗的，取得所有任務的執行結果
  - 因為40個任務超過了本Library裡執行緒池的最大數量(15)，所以可以藉此觀察`ExecutorSerice`是如何處理這樣的情形

2. 執行一個並行任務，做完第一個非同步操作後，再接著進行下一個，然後取得最後的執行結果

3. 執行6個任務，使其`Thread.sleep(2000)`模擬`I/O`操作，最後所有任務完成後的時間費時2秒，證明所有任務是並行進行的

### Gradle指令
  在terminal上，cd 到本專案資料夾底下後，執行
   - 編譯測試的java class
``` shell
    gradle testClasses
```

   - 執行測試
``` shell
      gradle test
```
   - 把上兩個一起做
``` shell
     gradle testClasses && gradle test
```
   - 執行測試，然後編譯出byteCode，會放在`build/classes/main`底下，打包起來的jar檔會放在`build/libs`底下，測試如果有問題，就`不會產生jar檔`
``` shell
     gradle build
```
   - 不執行測試，直接嘗試編譯出byte code跟jar檔
  ``` shell
    gradle build -x test
  ```
   - 先把`build資料夾`底下的檔案都先清空，再重新執行測試和打包
  ``` shell
    gradle clean build
  ```
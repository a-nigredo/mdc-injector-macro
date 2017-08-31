# MDC injector
Macro copy variables between MDC context if there is explicit logger call in def
  
Transformation example:  
```
@MdcInjector()
class A {
    def logMe = logger.info("log me msg")
}
```
Will be:  
```
@MdcInjector()
class A {
 def logMe = {
     val mdcCopy = org.slf4j.MDC.getCopyOfContextMap
     {
        org.slf4j.MDC.setContextMap(mdcCopy)
       logger.debug("msg")
     }
 }
}
```
Async transformation:
```
@MdcInjector()
class A {
    def logMe = Future(logger.info("log me msg"))
}
```
Will be: 
```
@MdcInjector()
class A {
 def logMe = {
     val mdcCopy = org.slf4j.MDC.getCopyOfContextMap
     Future {
       org.slf4j.MDC.setContextMap(mdcCopy)
       logger.debug("msg")
     }
  }
}
```
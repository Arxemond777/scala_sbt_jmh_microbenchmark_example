# scala(sbt)+jmh example

```
# Run benchmark (for scala)
sbt jmh:run -f1 -t1 -i 10 -wi 5
```

* -i 10   - we want to run each benchmark with 10 iterations  
* -wi 5   - 5 warmup iterations  
* -f1     - fork once on each benchmark  
* -t1     - says to run on one thread  
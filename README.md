# 🪁 Command handler
This library makes handling commands very easy.  
It's simple to add aliases, required permissions or specifying the command for only one type.

#🏷 Summary
* [Installation](#📀-Installation)
* [Dependencies](#📌-Dependencies)

# 📀 Installation
> **🐘 Gradle**
```gradle
maven {
    url 'https://m5rian.jfrog.io/artifactory/java'
}
```
```gradle
maven {
   implementation group: 'com.github.m5rian', name: 'JdaCommandHandler', version: 'VERSION'
}
```


> **〽 Maven**
```xml
<repository>
    <id>marian</id>
    <name>m5rian-java</name>
    <url>https://m5rian.jfrog.io/artifactory/java</url>
</repository>
```
```xml
<dependency>
    <groupId>com.github.m5rian</groupId>
    <artifactId>JdaCommandHandler</artifactId>
    <version>VERSION</version>
</dependency>
```

# 📌 Dependencies
* [JDA](https://github.com/DV8FromTheWorld/JDA)
* [Logback-classic](http://logback.qos.ch/)
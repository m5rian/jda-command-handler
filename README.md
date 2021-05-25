# 🪁 Command handler
This library makes handling commands very easy.  
It's simple to add aliases, required permissions or specifying the command for only one type.

## 🏷 Summary
* [Installation](#-installation)
* [Adding a basic command handler](#%EF%B8%8F-adding-a-basic-command-handler)
* [Dependencies](#-dependencies)

## 📀 Installation
`VERSION:` ![version]
<br/>
<br/>
### **🐘 Gradle**
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

## 🏗️ Adding a basic command handler
```java
// Your main class
public class Bot {
    // Create a new command service
    // You can make your own implementation or use the DefaultCommandService
    public static DefaultCommandService commandService = new DefaultCommandServiceBuilder()
            // Set a default prefix, which is used in dms
            .setDefaultPrefix("!")
            // Set a variable prefix which is used only in guilds
            // If you don't specify a variable prefix, the default prefix will be used instead
            .setVariablePrefix(guild -> Database.getGuild(guild).getPrefix())
            .build();

    public static void main(String[] args) {
        JDABuilder.createDefault(Config.get().getString("token"))
                // Register the CommandListener
                // Without it the bot won't respond on any commands
                .addEventListeners(new CommandListener(commandService))
                .build();

        // Register a command
        commandService.registerCommandClass(new Ping());
    }
}
```
You can create as many methods as you want. But make sure your methods have the `CommandEvent` annotation and  
that the class implements the `CommandHandler`!
```java
// A command class
public class Ping implements CommandHandler {

    @CommandEvent(
            name = "ping", // Set the main executor for the command
            aliases = {"latency"} // Add aliases
    )
    public void onHelpCommand(CommandContext ctx) {
        ctx.getChannel().sendMessage("Pong! Who cares about ping :)").queue(); // Send response
    }

}
```

## 📌 Dependencies
* [JDA](https://github.com/DV8FromTheWorld/JDA)
* [Logback-classic](http://logback.qos.ch/)

[version]: https://img.shields.io/maven-metadata/v?metadataUrl=https://m5rian.jfrog.io/artifactory/java/com/github/m5rian/JdaCommandHandler/maven-metadata.xml
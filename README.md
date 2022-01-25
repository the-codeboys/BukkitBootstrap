# BukkitBootstrap

A lightweight library for making Bukkit plugins

## Features

Currently BukkitBootstrap has the following features

- Config
- Gui
- CustomItems

## Download

You can download a jar from the [releases](https://github.com/the-codeboys/BukkitBootstrap/releases).
However I recommend to use jitpack to download it using maven or gradle. 
This also has the advantage that you can use the latest development version without having to build it yourself

[![](https://jitpack.io/v/the-codeboys/BukkitBootstrap.svg)](https://jitpack.io/#the-codeboys/BukkitBootstrap)

Please replace **VERSION** below with the version shown above!

**Maven**
```xml
<dependency>
	    <groupId>com.github.the-codeboys</groupId>
	    <artifactId>BukkitBootstrap</artifactId>
	    <version>VERSION</version>
	</dependency>
```
```xml
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```

**Gradle**
```gradle
dependencies {
	        implementation 'com.github.the-codeboys:BukkitBootstrap:VERSION'
	}

repositories {
			maven { url 'https://jitpack.io' }
		}
```

## Used by

- [MCIDE](https://github.com/the-codeboys/mcide): a working code editor that can run code directly in minecraft

> You're using BukkitBootstrap and want to see your project here? Just make an [issue](https://github.com/the-codeboys/BukkitBootstrap/issues/new) for it

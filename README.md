
Copy Maven Plugin
=================

A Maven Plugin to copy files from one path to another based on several `path` instructions.
Also replaces contents inside the copied files on the fly.

Inspired by the `goldin` `copy-maven-plugin` 

[![Build Status](https://travis-ci.org/Antibrumm/copy-maven-plugin.png)](https://travis-ci.org/Antibrumm/copy-maven-plugin)

Requirements
------------

 - Java 8
 - Maven 3.2.0+

Usage
-----

```xml
<plugin>
    <groupId>ch.mfrey.maven.plugin</groupId>
    <artifactId>copy-maven-plugin</artifactId>
    <version>0.0.2</version>
    <executions>
        <execution>
            <id>copy</id>
			<phase>generate-sources</phase>
			<goals>
				<goal>copy</goal>
			</goals>
			<configuration>
				<showfiles>true</showfiles>
				<resources>
					<resource>
						<directory>${project.build.directory}</directory>
						<move>true</move>
						<excludes>
							<exclude>**/*.java</exclude>
							<exclude>${some-artifactId}-${some-version}/src/**</exclude>
						</excludes>
						<paths>
							<path>
								<from>tmp</from>
								<to>output</to>
							</path>
						</paths>
					</resource>
					<resource>
						<directory>${project.build.directory}</directory>
						<move>true</move>
						<includes>
							<include>**/*.java</include>
						</includes>
						<excludes>
							<exclude>${some-artifactId}-${some-version}/src/**</exclude>
						</excludes>
						<paths>
							<path>
								<from>tmp</from>
								<to>output</to>
							</path>
							<path>
								<from>${escaped-groupId-folders}</from>
								<to>${new-groupId-folders}</to>
							</path>
							<path>
								<from>${escaped-artifactId}</from>
								<to>${new-artifactId}</to>
							</path>
						</paths>
						<replaces>
							<replace>
								<from>${escaped-groupId}</from>
								<to>${new-groupId}</to>
							</replace>
							<replace>
								<from>${escaped-artifactId}</from>
								<to>${new-artifactId}</to>
							</replace>
						</replaces>
					</resource>
				</resources>
			</configuration>
		</execution>
	</executions>
</plugin>
```

Options
-------
```xml
<resource>
	<directory>${project.build.directory}</directory>
	<charset>UTF-8</charset>
	<move>false</move>
	<workOnFullPath>false</workOnFullPath>
	<normalizePath>true</normalizePath>
	<includes>
		<include>**/*.*</include>
	</includes>
	<excludes>
		<exclude>none</exclude>
	</excludes>
	<paths>
		<path>
			<from>folder</from>
			<to>some/more/folders</to>
		</path>
	</paths>
	<replaces>
		<replace>
			<from>property</from>
			<to>newname</to>
		</replace>
	</replaces>						
</resource>
```

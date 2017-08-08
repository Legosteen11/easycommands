# EasyCommands
Add and parse commands easily for your Spigot Minecraft plugin.

## Examples
### Hello, World!
A very simple command with one optional parameter.
```kotlin
@Command("helloworld", "Use this command to display your name!")
data class HelloWorldCommand(
        @Argument("name", optional = true)
        val name: String = "World"
) : SimpleCommand {
    override fun execute(commandSender: CommandSender) {
        commandSender.sendMessage("Hello, $name")
    }
}
```
When the user executes `/helloworld`, the user will receive the message `Hello, World`. 

If they use the name parameter the field name will be set to that value. They don't have to provide a value for the parameter because: `optional = true`.
For example: `/helloworld Legosteen11` results in `Hello, Legosteen11`. 

If the user gives more parameters than needed, the values for the last parameters will be added together as one string. 
For example: `/helloworld Legosteen11, friends and contributors` results in `Hello, Legosteen11, friends and contributors`.

### More optional parameters
```kotlin
@Command("multiparamcommand", "Use this command to test multiple commands.")
data class MultiParamCommand (
        @Argument("name", optional = true)
        val name: String = "default1",
        @Argument("secondName")
        val secondName: String,
        @Argument("thirdName", optional = true)
        val thirdName: String = "default3"
) : SimpleCommand {
    override fun execute(commandSender: CommandSender) {
        commandSender.sendMessage("name = $name, secondName = $secondName, thirdName = $thirdName")
    }
}
```
Here the parameter `secondName` is not optional, but `name` and `thirdName` are. If the user only uses `/mutliparamcommand` an exception will be thrown that will be caught and translated to human readable text. This text will be sent to the user.

If the user does provide all the non-optional parameters, the command will execute as normal and use the default parameters for `name` and `thirdName`. So `/mutliparamcommand testvalue` results in `name = default1, secondName = testvalue, thirdName = default3`.

If the user provides two values, only one (the first) optional field will be set. For example: `/mutliparamcommand optional testvalue` results in `name = optional, secondName = testvalue, thirdName = default3`.

### Automatic type parsing
```kotlin
@Command("parsingcommand", "Use this command to test parsing commands.")
data class ParsingCommand (
        @Argument("int")
        val age: Int,
        @Argument("double")
        val double: Double,
        @Argument("double2")
        val double2: Double
) : SimpleCommand {
    override fun execute(commandSender: CommandSender) {
        println("age = $age")
    }
}
```
Here the fields are not of type `String`. The fields will be automatically parsed to ints and doubles. There is even support for parsing to an `OfflinePlayer`! You can also create your own parser for parsing to more types.

If a value cannot be parsed (for example: a user tries to use `test` for the field `int`) an exception will be thrown that will be caught and translated to human readable text. This text will be sent to the user

## How-to
### Step 1: Add the library
// TODO (the library is not yet available on any public repository, but you can compile the source yourself and use it from your local repository)

Maven:
```xml
<dependency>
    <groupId>io.github.legosteen11</groupId>
    <artifactId>easy-commands</artifactId>
    <version>x.x.x</version>
</dependency>
```
Gradle:
```groovy
compile 'io.github.legosteen11:easy-commands:x.x.x'
```

### Step 2: Create a CommandHandler instance
Add an instance of CommandHandler in your onEnable method.
```kotlin
override fun onEnable() {
    //...
    val commandHandler = CommandHandler()
    //...
}
```

### Step 3: Create your commands!
Now you can create your commands.

Every command has to have a primary constructor and a `Command` annotation. You must also implement `SimpleCommand` (or `SuperCommand` for supercommands) and override the function `execute(commandSender: CommandSender)`.

In the primary constructor you can add your fields. All fields must be annotated with the `Argument` annotation.

### Step 4: Register your commands
To register your commands simply call `CommandHandler#addCommands()`. You can provide your commands here. Do not forgot to add `::class` after your command name.

For example:
```kotlin
override fun onEnable() {
    //...
    val commandHandler = CommandHandler()
    commandHandler.addCommands(
        HelloWorldCommand::class,
        MutliParamCommand::class,
        ParsingCommand::class
    )
    //...
}
```
Now whenever someone executes one of your commands, they will be parsed and `#execute(commandSender: CommandSender)` will be called.
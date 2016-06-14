# How to use Plugins

Plugin is a set of functionality, i.e. Java classes packed into one or multiple JAR files.
The Server loads and initializes such JARs.

Loading and initializing are two separate steps of the process.

At the first step a part of the server, the [`PluginLoader`](http://smarttools.github.io/smartactors-core/apidocs/info/smart_tools/smartactors/core/plugin_loader_from_jar/PluginLoader.html) scans the JAR file for implementations of [`IPlugin`](http://smarttools.github.io/smartactors-core/apidocs/info/smart_tools/smartactors/core/iplugin/IPlugin.html).
It uses [`PluginCreator`](http://smarttools.github.io/smartactors-core/apidocs/info/smart_tools/smartactors/core/plugin_creator/PluginCreator.html) to instantiate the Plugin by passing the instance of `IBootstrap` to it's constructor.
The `load()` method of each Plugin is called here.

In it's `load()` method the Plugin adds it's own [`IBootstrapItem`](http://smarttools.github.io/smartactors-core/apidocs/info/smart_tools/smartactors/core/ibootstrap_item/IBootstrapItem.html) into the [`IBootstrap`](http://smarttools.github.io/smartactors-core/apidocs/info/smart_tools/smartactors/core/ibootstrap/IBootstrap.html) known to him.
The `IBootstrapItem` gives the name of the part of the functionality provided by the plugin and allows to declare dependencies from the other bootstrap items using the `after()` and `before()` methods.
Finally the sequence of the bootstrap items according to the dependencies is constructed and their `executeProcess()` is called.
This is the second step of the initialization.

![Plugins and Bootstrap](http://www.plantuml.com/plantuml/img/IyxFBSZFIyqhKGXEBIfBBU9AXWi4v9IcP-OX2JZbvvSKbnGb5c0Jyon9pUNYWXYYaA-hQwUWfAK4DKF1IY4dFp6b65KUhXKedLgHcbnQaWfK0TMX1JC1nGAWoeAY_BBC591AX7wSYfFpSt9IaqkG5ODba6s7AarAJSilIYMiBZ6j11XA0000)

Let create a sample plugin.

    public class MyPlugin implements IPlugin {
    
It must contain a reference to `Bootstrap` provided during initialization process.

    private final IBootstrap<IBootstrapItem<String>> bootstrap;
    
The `Bootstrap` must be injected into the plugin's constructor.

    public MyPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }
    
You have to define the `load()` method.

    @Override
    public void load() throws PluginException {
    
In the method at the first you should declare the `BootstrapItem` provided by your plugin.

    IBootstrapItem<String> item = new BootstrapItem("MyPlugin");
    
Here you declare that your item must be initialized after initialization of 'IOC' item.
 
    item.after("IOC");
    
Then you define your plugin initialization code.
For example, let register some new strategy in IOC.

    item.process(() -> {
        try {
            IKey<MyClass>key = IOC.resolve(IOC.getKeyForKeyStorage(), "new MyClass");
            IOC.register(key, new CreateNewInstanceStrategy(
                    (args) -> new MyClass((String) args[0])));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    });
    
Finally you should add your bootstrap item into `Bootstrap`: 

    bootstrap.add(item);
    
On the server side the loading of the plugin may looks as the following.

The server initializes the `Bootstrap` to handle the initialization sequence.

    IBootstrap bootstrap = new Bootstrap();
    
Also it uses `PluginCreator` to find and call the correct constructor of `IPlugin`.

    IPluginCreator creator = new PluginCreator();
    
A `IPluginLoaderVisitor` is necessary to track plugins loading process.

    IPluginLoaderVisitor<String> visitor = new MyPluginVisitor();
    
Because the plugin jars can be located in different directories and can be added dynamically, the special kind of [ClassLoader](http://docs.oracle.com/javase/8/docs/api/java/lang/ClassLoader.html) is required.
    
    ClassLoader urlClassLoader =
                    new ExpansibleURLClassLoader(new URL[]{}, ClassLoader.getSystemClassLoader());
                    
Then the `PluginLoader` is created.
It's second constructor parameter is the code to load each plugin.

    IPluginLoader<String> pluginLoader = new PluginLoader(
            urlClassLoader,
            (t) -> {
                try {
                    IPlugin plugin = creator.create(t, bootstrap);
                    plugin.load();
                } catch (Exception e) {
                    throw new RuntimeException("Could not create instance of IPlugin", e);
                }
            },
            visitor
    );
    
The server scans the jar file for `IPlugin` instances.

    pluginLoader.loadPlugin("examples.jar");
    
And then calls the initialization code of `BootstrapItem`s in the sequence according to their dependencies.

    bootstrap.start();
    
In this example, when we have two plugins: 'MyPlugin' following _after_ 'IOC' — the loading and initialization sequence is like this:
 
1. `IOCPlugin.load()`, registers bootstrap item
2. `MyPlugin.load()`, registers bootstrap item
3. initialization code of bootstrap item of 'IOC' plugin
4. initialization code of bootstrap item of 'MyPlugin' because it asked to be after 'IOC'

Note the order of first two steps is not defined, while the order of execution of bootstrap items is defined by their dependencies.

You can check the full source codes of this example [here](http://smarttools.github.io/smartactors-core/xref/info/smart_tools/smartactors/core/examples/plugin/package-summary.html).

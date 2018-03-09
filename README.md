# Importing an sbt project into IntelliJ

Before importing, configure IntelliJ IDEA to use the most recent Scala plugin, which includes sbt support (Use this plugin regardless of whether you are using the Java or the Scala API).

## Add the Scala plugin

To verify or add the JetBrains Scala plugin, follow these steps:

1. Open the **Settings** dialog:
    - If you have a project open, from the **File** menu, select **Settings**.
    - Otherwise, from the **Welcome** page **Configure** menu, select **Settings**. The **Settings** (or **Plugins**) dialog will open. If you open the **Settings**, select **Plugins**.
2. In the search box, enter `Scala`.
    - If you have it installed, you will see an option to uninstall it.
    - If you do not have it installed and it does not show in the list, click **Browse repositories…**. 
    - Scroll to find the appropriate plugin and click **Install**.
The dialog prompts you to restart IDEA.

## Import an sbt project

1. From the **Welcome Screen** or **File** menu, select **Open**.
2. Browse to and select the top-level folder of your sbt project, and click **OK**.
The **Import Project from SBT** dialog opens.
3. For import options:
- Enable **use auto-import**.
- For **Download** options, enable **Sources** and disable **Javadocs** and **Sources for SBT and plugins**.
- For **Project SDK**, verify the version (JDK 1.8 or higher).
- Leave Project format: at the default, .idea (directory based).
- Click **OK.**    
The first time you import an sbt project, the **SBT Project Data To Import** screen opens and asks you to confirm the import. IDEA opens the project. The status bar shows sbt downloading dependencies and refreshing the project.

You can build and run the project from the **Terminal** or create a **Run Configuration** as follows:
1. From the **Run** menu, select **Run**.
2. Click **Edit Configurations…**.
3. Click `+` (Add New Configuration).
4. Select **SBT Task**.
5. Name your configuration.
6. In the **Tasks** field, enter `runAll`.
7. Click Run.


These instructions can also be found here: https://www.lagomframework.com/documentation/1.4.x/java/IntellijSbtJava.html

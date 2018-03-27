# NetBeans WordPress Plugin

This is NetBeans plugin for WordPress.

## How to enable

`enabled` option is unchecked by default. Please check it on `project properties > Frameworks > WordPress`

## Requirements

- NetBeans 8.2+
- WordPress 3.5+

## Features

- badge icon
- important files
- create new WordPress project
- code templates
- zip compress action
- template files
- display and change debug status
- code completion for filter and action
- create new theme action
- hyperlink navigation
- create new plugin action
- custom content name
- run command (wp-cli)
- upgrade notification
- create a .htaccess file for permalink
- create a new child theme action (create a style.css for child theme)

### Important Files

- wp-config.php
- .htaccess (only the root directory)

### Create New WordPress Project

You can create a new WordPress project via a wizard.

1. Set Tools > Options > PHP > WordPress > download url(e.g. http://wordpress.org/latest.zip) or local file path(e.g. /path/to/wordpress.zip)
2. Click File > New Project
3. Check PHP > PHP Application > ... > Framework > WordPress PHP Web Blog/CMS
4. Select options (url / local file / wp-cli)
5. Set wp-config options
6. Click the Finish button

#### options

- Set format options for WordPress to this project : add format options for WordPress to project.properties
- Create a wp-config.php : copy from wp-config-sample.php

### Custom Content Name

If you want to use a custom content name (default value is `wp-content`), please set your content name to the project properties.
(project properties > Framework > WordPress)
If your wp-content directory is not in your WordPress Root, please set a relative path (from a source directory to wp-content directory) to `wp-content`.

### Code Templates

- wpph : wordpress plugin header
- wpgpl : wordpress license comment

e.g. please type wpph [Tab]

### Zip Compress Action

You can compress specified pluign or theme directory as Zip file to the same hierarchy.  
Right-click active plugin or theme node > Zip compress

### Template Files

You can create a pluign file and readme file with new file wizard.

Right-click a directory > New > Others > WordPress > (WordPress Plugin | WordPress Plugin Readme)

### Code Completion for Filter and Action

This feature is available the followings:
- add_filter, remove_filter
- add_acttion, remove_action

first parameter: action or filter name, second parameter: function name

For example:
``` php
add_action('[Ctrl + Space]', 'function_name');
add_filter('the_content', 'w[Ctrl + Space]'); // e.g. start with 'w'
```

### Display And Change Debug Status

WP_DEBUG value(wp-config.php) is displayed on bottom-right of IDE. 
If you click there, you can change WP_DEBUG value.
WordPress version number is also displayed.

### Create New Theme Action

Right-click a WordPress project node > WordPress > Create Theme

#### Minimum Theme

Just create a style.css and an empty index.php to the directory which you named.

#### Underscores

Create a theme from [Underscores | A Starter Theme for WordPress](http://underscores.me/). Underscores is awesome!
This plugin uses [Automattic/_s · GitHub](https://github.com/automattic/_s).

**Please note that license of created theme is GPLv2**

#### Barebones

Create a theme form [welcomebrand/Barebones · GitHub](https://github.com/welcomebrand/Barebones).

### Create New Child Theme Action

Right-click a WordPress project node > WordPress > Create Child Theme

- Create a new directory for child theme
- Add style.css for child theme

### Create New Plugin Action

Right-click Project > WordPress > Create Plugin

This is very simple feature. If you input a plugin name (plugin_name)...

- Create a new plugin directory (plugins/plugin_name)
- Add a main plugin file there (plugins/plugin_name/plugin_name.php)
- Add a readme file there (plugins/plugin_name/readme.txt)

### Hyperlink Navigation

This feature is available on the parameters of following functions:
- add_filter, remove_filter
- add_action, remove_action

Hold down Ctrl key on first or second parameter. If text color is changed to blue, Click there.  
Then caret position go to function. (first parameter is available when there are the same function names as parameter name : in this case, caret doesn't necessarily go to right position)

### Run Command

You can run wp-cli commands. *Please note that this action needs so much time to get command list at first time.*

1. Set wp-cli script path to Options. Tools > Options > PHP > WordPress
2. Right-click a WordPress project node > WordPress > RunCommand...

#### wp-cli

Please see the following links:

- https://github.com/wp-cli/wp-cli
- http://wp-cli.org/

### Upgrade Notification

Check whether new versions (for core, plugin and theme) are available when a WordPress project is opened.
Furthermore, you can upgrade(run core update, core update-db, e.t.c.) WordPress from a notification window if we are setting wp-cli.

If you don't want to check that, please uncheck `Check new version when project is opened` at Options.

### Create a .htaccess file for permalink

Right-click a WordPress project node > WordPress > Create .htaccesss for permalink

#### Note

.htaccess file for permalink can be also created with template.

## Version Number

|       |stable |dev      |
|:------|:-----:|:-------:|
|pattern| n.n.n | n.n.n.n |
|e.g.   | 1.0.1 | 1.0.1.2 |

### Stable version

Available on [Plugin Portal](http://plugins.netbeans.org/plugin/46542/php-wordpress-blog-cms).

### Development version

If development version exists, it will be available in the same archive as stable version.

### Downloads

[github releases](https://github.com/junichi11/netbeans-wordpress-plugin/releases)

## License

[Common Development and Distribution License (CDDL) v1.0 and GNU General Public License (GPL) v2](http://netbeans.org/cddl-gplv2.html)

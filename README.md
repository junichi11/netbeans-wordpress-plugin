# NetBeans WordPress Plugin

This is the NetBeans IDE plugin for WordPress.

## How to enable

The `enabled` option is unchecked by default. Please check it on `Project Properties > Frameworks > WordPress`

## Environment
- NetBeans 8.0+
- WordPress 3.5+

## Features
- Badge icon
- Important files
- Create new WordPress project
- Code templates
- Zip compress action
- Template files
- Display and change debug status
- Code completion for filter and action
- Create new theme action
- Hyperlink navigation
- Create new plugin action
- Custom content name
- Run command (wp-cli)
- Upgrade notification
- Create a .htaccess file for permalink
- Create a new child theme action (create a style.css for child theme)

### Important Files
It contains the wp-config.php

### Create New WordPress Project
You can create a new WordPress project using the Wizard.

1. Set Tools > Options > PHP > WordPress > download url(e.g. http://wordpress.org/latest.zip) or local file path(e.g. /path/to/wordpress.zip)
2. Check New Project > PHP > PHP Application > ... > Framework > WordPress PHP Web Blog/CMS
3. Select options (url / local file / wp-cli)
4. Finished

### Custom Content Name

If you want to use custom content name(default is `wp-content`), please set your content name to the project properties.
(Project Properties > Framework > WordPress)

#### Options
- Set format to project : set format option to project.properties
- create wp-config.php : copy from wp-config-sample.php

### Code Templates
- wpph : wordpress plugin header
- wpgpl : wordpress license comment

e.g. please type wpph [Tab]

### Zip Compress Action
You can compress specified pluign or theme directory as Zip file to the same hierarchy.  
Right-click active plugin or theme node > Zip compress

### Template Files
You can create a pluign file and readme file with new file wizard.

Right-click directory > New > Others > WordPress > (WordPress Plugin | WordPress Plugin Readme)

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
WP_DEBUG value(wp-config.php) is displayed on bottome-right of IDE. 
If you click there, you can change WP_DEBUG value.
WordPress version number is also displayed.

### Create New Theme Action
Right-click Project > WordPress > Create Theme

#### Underscores
Create theme from [Underscores | A Starter Theme for WordPress](http://underscores.me/). Underscores is awesome!
This plugin uses [Automattic/_s · GitHub](https://github.com/automattic/_s).

**Please notice that license of created theme is GPLv2**

#### Barebones
Create theme form [welcomebrand/Barebones · GitHub](https://github.com/welcomebrand/Barebones).

### Create New Child Theme Action
Right-click Project > WordPress > Create Child Theme

- Create a new directory for child theme
- Add style.css for child theme

### Create New Plugin Action
Right-click Project > WordPress > Create Plugin

This is very simple feature.
- input plugin name(plugin_name)
- create new plugin directory(plugins/plugin_name)
- add main plugin file (plugins/plugin_name/plugin_name.php)
- add readme file (plugins/plugin_name/readme.txt)

### Hyperlink Navigation
This feature is available the followings:
- add_filter, remove_filter
- add_action, remove_action

Hold down Ctrl key on first or second parameter. If text color is changed to blue, Click there.  
Then caret position go to function. (first parameter is available when there are the same function names as parameter name : in this case, caret doesn't necessarily go to right position)

### Run Command

We can run wp-cli commands. *Please notice that this action needs so much time to get command list at first time.*

1. Set wp-cli script path to Options. Tools > Options > PHP > WordPress
2. Select WordPress Project node
3. Right-click > WordPress > RunCommand...

#### WP-CLI

Please see the following:

- https://github.com/wp-cli/wp-cli
- http://wp-cli.org/

### Upgrade Notification

Check whether new versions (for core, plugin and theme) are available when WordPress project is opened.
Furthermore, we can upgrade(run core update, core update-db, e.t.c.) WordPress with notification window if we are setting wp-cli.

If you don't want to check that, please uncheck `Check new version when project is opened` at Options.

### Create a .htaccess file for permalink

Right-click project node > WordPress > Create .htaccesss for permalink

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

If a development version exists, it will be available in the same archive as the stable version.

### Archive

[Github releases](https://github.com/junichi11/netbeans-wordpress-plugin/releases)

## License
[Common Development and Distribution License (CDDL) v1.0 and GNU General Public License (GPL) v2](http://netbeans.org/cddl-gplv2.html)

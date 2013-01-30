# About
This is NetBeans plugin for WordPress.

## Environment
- NetBeans 7.3+
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

### Impotant Files
It contains wp-config.php

### Create New WordPress Project
You can create a new WordPress project with Wizard.

1. set Tools > Options > PHP > WordPress > download url(e.g. http://wordpress.org/latest.zip) or local file path(e.g. /path/to/wordpress.zip)
2. check New Project > PHP > PHP Application > ... > Framework > WordPress PHP Web Blog/CMS
3. choose options
4. finish

#### options
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
Since this is not good yet, this feature have to be improved.

### Display And Change Debug Status
WP_DEBUG value(wp-config.php) is displayed on bottome-right of IDE. 
If you click there, you can change WP_DEBUG value.

### Create New Theme Action
Right-click Project > WordPress > Create Theme (_s)

Create theme from [Underscores | A Starter Theme for WordPress](http://underscores.me/). Underscores is awesome!
This plugin uses [Automattic/_s · GitHub](https://github.com/automattic/_s).

**Please notice that license of created theme is GPLv2**

## License
[Common Development and Distribution License (CDDL) v1.0 and GNU General Public License (GPL) v2](http://netbeans.org/cddl-gplv2.html)

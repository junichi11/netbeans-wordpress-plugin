@TemplateRegistrations({
    @TemplateRegistration(
            folder = "Licenses",
            iconBase = WordPress.WP_ICON_16,
            displayName = "#WordPress_License_Template_DisplayName",
            content = "license-wp-gpl20.txt",
            description = "WpGPLv2LicenseDescription.html",
            scriptEngine = "freemarker"),
    @TemplateRegistration(
            folder = "WordPress",
            category = "PHP",
            iconBase = WordPress.WP_ICON_16,
            displayName = "#WordPress_Plugin_Template_DisplayName",
            content = "WpPlugin.php",
            description = "WpPluginDescription.html",
            scriptEngine = "freemarker"),
    @TemplateRegistration(
            folder = "WordPress",
            category = "PHP",
            iconBase = WordPress.WP_ICON_16,
            displayName = "#WordPress_Plugin_Readme_DisplayName",
            content = "readme.txt",
            description = "WpPluginReadmeDescription.html",
            scriptEngine = "freemarker"),
    @TemplateRegistration(
            folder = "WordPress",
            category = "PHP",
            iconBase = WordPress.WP_ICON_16,
            displayName = "#WordPress_Theme_Style_DisplayName",
            content = "style.css",
            description = "WpThemeStyleDescription.html",
            scriptEngine = "freemarker"),
    @TemplateRegistration(
            folder = "WordPress",
            category = "PHP",
            iconBase = WordPress.WP_ICON_16,
            displayName = "#WordPress_Child_Theme_Style_DisplayName",
            content = "child-style.css",
            description = "WpChildThemeStyleDescription.html",
            scriptEngine = "freemarker"),
    @TemplateRegistration(
            folder = "WordPress",
            category = "PHP",
            //            iconBase = WordPress.WP_ICON_16,
            displayName = "#WordPress_Permalink_Htaccess_DisplayName",
            content = ".htaccess",
            description = "WpPermalinkHtaccessDescription.html",
            scriptEngine = "freemarker"),})
@NbBundle.Messages({
    "WordPress_License_Template_DisplayName=GPLv2",
    "WordPress_Plugin_Template_DisplayName=WordPress Plugin File",
    "WordPress_Plugin_Readme_DisplayName=WordPress Plugin Readme File",
    "WordPress_Theme_Style_DisplayName=WordPress Theme Style File",
    "WordPress_Child_Theme_Style_DisplayName=WordPress Child Theme Style File",
    "WordPress_Permalink_Htaccess_DisplayName=WordPress .htaccess file for permalink"})
package org.netbeans.modules.php.wordpress.resources.templates;

import org.netbeans.api.templates.TemplateRegistration;
import org.netbeans.api.templates.TemplateRegistrations;
import org.netbeans.modules.php.wordpress.WordPress;
import org.openide.util.NbBundle;

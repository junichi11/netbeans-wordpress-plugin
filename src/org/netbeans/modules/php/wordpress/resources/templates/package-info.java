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
            iconBase = WordPress.WP_ICON_16,
            displayName = "#WordPress_Plugin_Template_DisplayName",
            content = "WpPlugin.php",
            description = "WpPluginDescription.html",
            scriptEngine = "freemarker"),
    @TemplateRegistration(
            folder = "WordPress",
            iconBase = WordPress.WP_ICON_16,
            displayName = "#WordPress_Plugin_Readme_DisplayName",
            content = "readme.txt",
            description = "WpPluginReadmeDescription.html",
            scriptEngine = "freemarker")})
@NbBundle.Messages({
    "WordPress_License_Template_DisplayName=GPLv2",
    "WordPress_Plugin_Template_DisplayName=WordPress Plugin",
    "WordPress_Plugin_Readme_DisplayName=WordPress Plugin Readme"
})
package org.netbeans.modules.php.wordpress.resources.templates;

import org.netbeans.api.templates.TemplateRegistration;
import org.netbeans.api.templates.TemplateRegistrations;
import org.netbeans.modules.php.wordpress.WordPress;
import org.openide.util.NbBundle;

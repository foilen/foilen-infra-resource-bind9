/*
    Foilen Infra Resource Bind9
    https://github.com/foilen/foilen-infra-resource-bind9
    Copyright (c) 2018 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.infra.resource.bind9;

import java.util.Arrays;
import java.util.Collections;

import com.foilen.infra.plugin.v1.core.context.CommonServicesContext;
import com.foilen.infra.plugin.v1.core.plugin.IPPluginDefinitionProvider;
import com.foilen.infra.plugin.v1.core.plugin.IPPluginDefinitionV1;

public class FoilenBind9PluginDefinitionProvider implements IPPluginDefinitionProvider {

    @Override
    public IPPluginDefinitionV1 getIPPluginDefinition() {
        IPPluginDefinitionV1 pluginDefinition = new IPPluginDefinitionV1("Foilen", "DNS - Bind9", "A Bind9 DNS server that uses all the DnsEntries", "1.0.0");

        pluginDefinition.addCustomResource(Bind9Server.class, Bind9Server.RESOURCE_TYPE, //
                Arrays.asList(Bind9Server.PROPERTY_NAME), //
                Collections.emptyList());

        // Resource editors
        pluginDefinition.addTranslations("/com/foilen/infra/resource/bind9/messages");
        pluginDefinition.addResourceEditor(new Bind9ServerEditor(), Bind9ServerEditor.EDITOR_NAME);

        // Updater Handler
        pluginDefinition.addUpdateHandler(new Bind9UpdateEventHandler());
        pluginDefinition.addUpdateHandler(new Bind9DnsEntryUpdateEventHandler());

        return pluginDefinition;
    }

    @Override
    public void initialize(CommonServicesContext commonServicesContext) {
    }

}

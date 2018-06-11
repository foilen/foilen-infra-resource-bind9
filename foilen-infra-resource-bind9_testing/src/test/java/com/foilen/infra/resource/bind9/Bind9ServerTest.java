/*
    Foilen Infra Resource Bind9
    https://github.com/foilen/foilen-infra-resource-bind9
    Copyright (c) 2018 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.infra.resource.bind9;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

import org.junit.Test;

import com.foilen.infra.plugin.core.system.fake.junits.AbstractIPPluginTest;
import com.foilen.infra.plugin.core.system.junits.JunitsHelper;
import com.foilen.infra.plugin.v1.core.context.ChangesContext;
import com.foilen.infra.plugin.v1.model.base.IPApplicationDefinition;
import com.foilen.infra.plugin.v1.model.resource.LinkTypeConstants;
import com.foilen.infra.resource.application.Application;
import com.foilen.infra.resource.bind9.Bind9Server;
import com.foilen.infra.resource.bind9.Bind9ServerEditor;
import com.foilen.infra.resource.machine.Machine;
import com.foilen.infra.resource.unixuser.UnixUser;
import com.foilen.smalltools.tools.JsonTools;

public class Bind9ServerTest extends AbstractIPPluginTest {

    @Test
    public void test() {

        // Create resources
        Machine machine = new Machine("h1.example.com", "192.168.0.200");
        UnixUser unixUser = new UnixUser(2000, "infra-bind", "/home/infra-bind", null, null);

        Bind9Server bind9Server = new Bind9Server();
        bind9Server.setAdminEmail("admin@example.com");
        bind9Server.setName("myDns");
        bind9Server.setNsDomainNames(Arrays.asList("ns1.example.com", "ns2.example.com").stream().collect(Collectors.toSet()));
        bind9Server.setResourceEditorName(Bind9ServerEditor.EDITOR_NAME);

        ChangesContext changes = new ChangesContext(getCommonServicesContext().getResourceService());
        changes.resourceAdd(machine);
        changes.resourceAdd(unixUser);
        changes.resourceAdd(bind9Server);

        // Create links
        changes.linkAdd(bind9Server, LinkTypeConstants.RUN_AS, unixUser);
        changes.linkAdd(bind9Server, LinkTypeConstants.INSTALLED_ON, machine);

        // Execute
        getInternalServicesContext().getInternalChangeService().changesExecute(changes);

        // Change serial
        SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd");
        String currentSerial = sdf.format(new Date()) + "00";
        String newSerial = "1111111100";
        Application application = fakeSystemServicesImpl.getResources().stream() //
                .filter(it -> it.getClass().equals(Application.class) && "myDns_bind9".equals(it.getResourceName())) //
                .map(it -> (Application) it) //
                .findAny().get();
        String json = JsonTools.compactPrint(application.getApplicationDefinition());
        application.setApplicationDefinition(JsonTools.readFromString(json.replaceAll(currentSerial, newSerial), IPApplicationDefinition.class));

        // Assert
        JunitsHelper.assertState(getCommonServicesContext(), getInternalServicesContext(), "Bind9ServerTest-state.json", getClass(), true);
    }

}

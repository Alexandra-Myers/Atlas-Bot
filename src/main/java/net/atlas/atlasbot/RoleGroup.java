package net.atlas.atlasbot;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.internal.entities.RoleImpl;

import java.util.ArrayList;

public class RoleGroup extends RoleImpl {
    public RoleGroup(long id, Guild guild) {
        super(id, guild);
        startLoop();
    }
    public ArrayList<Role> rolesInGroup = new ArrayList<Role>();
    public void startLoop() {
        for (int i = 0; i <= 201; i++) {
            if(i == 200) {
                for (Role role : rolesInGroup) {
                    this.setColor(role.getColorRaw());
                }
                i = 0;
            }
        }
    }
}

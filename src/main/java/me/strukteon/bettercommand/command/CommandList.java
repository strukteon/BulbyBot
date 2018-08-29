package me.strukteon.bettercommand.command;
/*
    Created by nils on 09.08.2018 at 20:17.
    
    (c) nils 2018
*/

import java.util.ArrayList;

public class CommandList extends ArrayList<BaseCommand> {

    @Override
    public boolean add(BaseCommand baseCommand) {
        return super.add(baseCommand);

    }
}

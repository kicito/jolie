/*
 * Copyright (C) 2020 Narongrit Unwerawattana <narongrit.kie@gmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */

package jolie.lang.parse.module;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import jolie.lang.parse.ParserException;
import jolie.lang.parse.module.exceptions.ModuleNotFoundException;

public class ModuleCrawler
{

    private final Queue< Source > modulesToCrawl;
    private final Map< URI, ModuleRecord > moduleCrawled;
    private final String[] packagesPath;
    private final FinderCreator finderCreator;

    public ModuleCrawler( String[] packagesPath ) throws FileNotFoundException
    {
        this.modulesToCrawl = new LinkedList<>();
        this.moduleCrawled = new HashMap<>();
        this.packagesPath = packagesPath;
        this.finderCreator = new FinderCreator( packagesPath );
    }

    public ModuleCrawler( Path workingDirectory, String[] packagesPath )
            throws FileNotFoundException
    {
        this.modulesToCrawl = new LinkedList<>();
        this.moduleCrawled = new HashMap<>();
        this.packagesPath = packagesPath;
        this.finderCreator = new FinderCreator( workingDirectory, packagesPath );
    }

    private Source findModule( URI parentURI, String[] importTargetStrings )
            throws ModuleNotFoundException
    {
        Finder finder = finderCreator.getFinderForTarget( parentURI, importTargetStrings );
        Source targetFile = finder.find();
        return targetFile;
    }

    private void crawlModule( ModuleRecord record ) throws ModuleException
    {
        for (SymbolInfoExternal externalSymbol : record.symbolTable().externalSymbols()) {
            try {
                Source moduleSource =
                        this.findModule( record.source(), externalSymbol.moduleTargets() );
                externalSymbol.setModuleSource( moduleSource );
                modulesToCrawl.add( moduleSource );
            } catch (ModuleNotFoundException e) {
                throw new ModuleException(externalSymbol.context(), e);
            }
        }
        moduleCrawled.put( record.source(), record );
    }

    public Set< ModuleRecord > crawl( ModuleRecord parentRecord, ModuleParser parser )
            throws ParserException, IOException, ModuleException
    {

        // start with parentRecord
        crawlModule( parentRecord );

        // walk through dependencies
        while (modulesToCrawl.peek() != null) {
            Source module = modulesToCrawl.poll();

            if ( moduleCrawled.containsKey( module.source() ) ) {
                continue;
            }

            ModuleRecord p = parser.parse( module );
            crawlModule( p );
        }
        return new HashSet< ModuleRecord >( moduleCrawled.values() );
    }
}

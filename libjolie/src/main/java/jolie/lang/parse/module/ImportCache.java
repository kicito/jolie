package jolie.lang.parse.module;

import java.net.URI;

class ImportCache
{
    public enum Status {
        PENDING, FINISHED
    }

    URI source;
    ModuleRecord rc;
    Status s;

    public ImportCache( URI source )
    {
        this.source = source;
        s = Status.PENDING;
    }

    public void setModuleRecord(ModuleRecord rc){
        this.rc = rc;
    }

    public void importFinished()
    {
        s = Status.FINISHED;
    }
}

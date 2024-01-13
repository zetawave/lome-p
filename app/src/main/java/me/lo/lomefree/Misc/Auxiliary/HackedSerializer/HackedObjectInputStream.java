package me.lo.lomefree.Misc.Auxiliary.HackedSerializer;


import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class HackedObjectInputStream extends ObjectInputStream {

    public HackedObjectInputStream(InputStream in) throws IOException {
        super(in);
    }

    @Override
    protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
        ObjectStreamClass resultClassDescriptor = super.readClassDescriptor();
        if (resultClassDescriptor.getName().equals("me.lo.lome.Keys.Entities.KeyFile"))
            resultClassDescriptor = ObjectStreamClass.lookup(me.lo.lomefree.Keys.Entities.KeyFile.class);
        else if(resultClassDescriptor.getName().equals("me.lo.lome.Utils.Files.Entities.HiderItem"))
            resultClassDescriptor = ObjectStreamClass.lookup(me.lo.lomefree.Utils.Files.Entities.HiderItem.class);
        return resultClassDescriptor;
    }
}

package io.papermc.paper.plugin.entrypoint.classloader;

import io.papermc.paper.plugin.configuration.PluginMeta;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

// Stub, implement in future.
public class PaperClassloaderBytecodeModifier implements ClassloaderBytecodeModifier {

    @Override
    public byte[] modify(PluginMeta configuration, byte[] bytecode) {
        ClassReader classReader = new ClassReader(bytecode);
        ClassWriter classWriter = new ClassWriter(classReader, 0);
        classReader.accept(io.papermc.paper.pluginremap.reflect.ReflectionRemapper.visitor(classWriter), 0);
        return classWriter.toByteArray();
    }
}

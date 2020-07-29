package net.md_5.bungee.jni;

import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;
import net.md_5.bungee.jni.cipher.BungeeCipher;

public final class NativeCode<T>
{

    private final String name;
    private final Supplier<? extends T> javaImpl;
    private final Supplier<? extends T> nativeImpl;
    private Supplier<? extends T> impl;
    //
    private boolean loaded;

    public NativeCode(String name, Supplier<? extends T> javaImpl, Supplier<? extends T> nativeImpl)
    {
        this.name = name;
        this.javaImpl = javaImpl;
        this.nativeImpl = nativeImpl;
    }

    @Deprecated
    public NativeCode(String name, Class<? extends T> javaImpl, Class<? extends T> nativeImpl)
    {
        this( name, newSupplier( javaImpl ), newSupplier( nativeImpl ) );
    }

    public T newInstance()
    {
        return impl.get();
    }

    public boolean load()
    {
        if ( !loaded && isSupported() )
        {
            String fullName = "bungeecord-" + name;

            try
            {
                System.loadLibrary( fullName );
                loaded = true;
            } catch ( Throwable t )
            {
            }

            if ( !loaded )
            {
                try ( InputStream soFile = BungeeCipher.class.getClassLoader().getResourceAsStream( name + ".so" ) )
                {
                    // Else we will create and copy it to a temp file
                    File temp = File.createTempFile( fullName, ".so" );
                    // Don't leave cruft on filesystem
                    temp.deleteOnExit();

                    try ( OutputStream outputStream = new FileOutputStream( temp ) )
                    {
                        ByteStreams.copy( soFile, outputStream );
                    }

                    System.load( temp.getPath() );
                    loaded = true;
                } catch ( IOException ex )
                {
                    // Can't write to tmp?
                } catch ( UnsatisfiedLinkError ex )
                {
                    System.out.println( "Could not load native library: " + ex.getMessage() );
                }
            }
        }

        impl = ( loaded ) ? nativeImpl : javaImpl;
        return loaded;
    }

    public static boolean isSupported()
    {
        return "Linux".equals( System.getProperty( "os.name" ) ) && "amd64".equals( System.getProperty( "os.arch" ) );
    }

    private static <T> Supplier<? extends T> newSupplier(Class<? extends T> clazz)
    {
        Constructor<? extends T> constructor;
        try
        {
            constructor = clazz.getConstructor();
        } catch ( NoSuchMethodException e )
        {
            throw new Error( e );
        }
        constructor.setAccessible( true );
        Constructor<? extends T> result = constructor;
        return new Supplier<T>()
        {
            @Override
            public T get()
            {
                try
                {
                    return result.newInstance();
                } catch ( InstantiationException | IllegalAccessException e )
                {
                    throw new Error( e );
                } catch ( InvocationTargetException e )
                {
                    throw new Error( e.getTargetException() );
                }
            }
        };
    }
}

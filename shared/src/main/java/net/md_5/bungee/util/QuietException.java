package net.md_5.bungee.util;

/**
 * Exception without a stack trace component.
 */
public class QuietException extends RuntimeException
{

    public QuietException(String message)
    {
        super( message );
    }

    public QuietException(String message, Throwable cause)
    {
        super( message, cause );
    }

    public QuietException(Throwable cause)
    {
        super( cause );
    }

    public QuietException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super( message, cause, enableSuppression, writableStackTrace );
    }

    @Override
    public Throwable initCause(Throwable cause)
    {
        return this;
    }

    @Override
    public Throwable fillInStackTrace()
    {
        return this;
    }
}

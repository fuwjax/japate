package org.fuwjax.japate.sample.dispatch;

import org.fuwjax.japate.iteration.Reiterable;

/**
 * Message
 */
public class Message {
    private static class Curry extends Message {
        private Message target;
        private Object[] curriedArgs;

        public Curry(Message target, Object... args) {
            // instead we should filter the target actions with the new information
            super(Reiterable.empty());
            this.target = target;
            this.curriedArgs = args;
        }
        
        public Object dispatch(Object... args) throws DispatchException {
            Object[] newArgs = new Object[this.curriedArgs.length + args.length];
            System.arraycopy(this.curriedArgs, 0, newArgs, 0, this.curriedArgs.length);
            System.arraycopy(args, 0, newArgs, this.curriedArgs.length, args.length);
            return target.dispatch(newArgs);
        }
    }

    private Reiterable<Action> actions;

    public Message(Reiterable<Action> actions) {
        this.actions = actions;
    }

    public Object dispatch(Object... args) throws DispatchException{
        DispatchException attempts = new DispatchException();
        for(Action action: actions) {
            try {
                return action.attempt(args);
            } catch(Exception e) {
                attempts.failed(e);
            }
        }
        throw attempts;
    }

    public Message with(Object... args) {
        return new Curry(this, args);
    }
}

package org.jclouds.azurecomputearm.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Objects;
import java.beans.ConstructorProperties;

/**
 * Created by jtjk on 25.2.2016.
 */
public class Subscription {
    public static Builder<?> builder() {
        return new ConcreteBuilder();
    }

    public Builder<?> toBuilder() {
        return new ConcreteBuilder().fromSubscription(this);
    }

    public abstract static class Builder<T extends Builder<T>> {
        protected abstract T self();

        protected String id;
        protected String subscriptionId;
        protected String displayName;
        protected String state;

        /**
         * @see Subscription#getId()
         */
        public T id(String id) {
            this.id = checkNotNull(id, "id");
            return self();
        }

        /**
         * @see Subscription#getSubscriptionId()
         */
        public T subscriptionId(String subscriptionId) {
            this.subscriptionId = checkNotNull(subscriptionId, "subscriptionId");
            return self();
        }

        /**
         * @see Subscription#getState()
         */
        public T state(String state) {
            this.state = checkNotNull(state, "state");
            return self();
        }

        /**
         * @see Subscription#getDisplayName()
         */
        public T displayName(String displayName) {
            this.displayName = checkNotNull(displayName, "displayName");
            return self();
        }

        public Subscription build() {
            return new Subscription(id, subscriptionId, displayName, state);
        }

        public T fromSubscription(Subscription in) {
            return this.id(in.getId()).subscriptionId(in.getSubscriptionId()).displayName(in.getDisplayName())
                    .state(in.getState());
        }
    }

    private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
        @Override
        protected ConcreteBuilder self() {
            return this;
        }
    }

    private final String id;
    private final String subscriptionId;
    private final String displayName;
    private final String state;

    @ConstructorProperties({
            "id", "subscriptionId", "displayName", "state"
    })
    protected Subscription(String id, String subscriptionId, String displayName, String state) {
        this.id = checkNotNull(id, "id");
        this.subscriptionId = checkNotNull(subscriptionId, "subscriptionId");
        this.displayName = checkNotNull(displayName, "displayName");
        this.state = checkNotNull(state, "state");
    }

    /**
     * @return the generated id of the subscription
     */
    public String getId() {
        return this.id;
    }

    /**
     * @return the subscriptionId of the subscription
     */
    public String getSubscriptionId() {
        return this.subscriptionId;
    }

    /**
     * @return name of the subscription
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * @return the state of the subscription
     */
    public String getState() {
        return this.state;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Subscription that = Subscription.class.cast(obj);
        return Objects.equal(this.id, that.id);
    }

    protected Objects.ToStringHelper string() {
        return Objects.toStringHelper("").add("id", id).add("subscriptionId", subscriptionId).add("displayName", displayName)
                .add("state", state);
    }

    @Override
    public String toString() {
        return string().toString();
    }

    public static Subscription create(final String id, final String subscriptionId, final String displayName,
                                  final String state) {

        return new Subscription(id, subscriptionId, displayName, state);
    }

}

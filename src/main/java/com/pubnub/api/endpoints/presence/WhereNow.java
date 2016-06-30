package com.pubnub.api.endpoints.presence;

import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubException;
import com.pubnub.api.builder.PubNubErrorBuilder;
import com.pubnub.api.endpoints.Endpoint;
import com.pubnub.api.enums.PNOperationType;
import com.pubnub.api.models.consumer.presence.PNWhereNowResult;
import com.pubnub.api.models.server.Envelope;
import com.pubnub.api.models.server.presence.WhereNowPayload;
import lombok.Setter;
import lombok.experimental.Accessors;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.util.Map;

@Accessors(chain = true, fluent = true)
public class WhereNow extends Endpoint<Envelope<WhereNowPayload>, PNWhereNowResult> {

    @Setter
    private String uuid;

    public WhereNow(PubNub pubnub, Retrofit retrofit) {
        super(pubnub, retrofit);
    }

    @Override
    protected void validateParams() throws PubNubException {
        if (this.getPubnub().getConfiguration().getSubscribeKey() == null || this.getPubnub().getConfiguration().getSubscribeKey().isEmpty()) {
            throw PubNubException.builder().pubnubError(PubNubErrorBuilder.PNERROBJ_SUBSCRIBE_KEY_MISSING).build();
        }
    }

    @Override
    protected Call<Envelope<WhereNowPayload>> doWork(Map<String, String> params) {
        PresenceService service = this.getRetrofit().create(PresenceService.class);
        return service.whereNow(this.getPubnub().getConfiguration().getSubscribeKey(),
                this.uuid != null ? this.uuid : this.getPubnub().getConfiguration().getUuid(), params);
    }

    @Override
    protected PNWhereNowResult createResponse(Response<Envelope<WhereNowPayload>> input) throws PubNubException {
        if (input.body() == null || input.body().getPayload() == null) {
            throw PubNubException.builder().pubnubError(PubNubErrorBuilder.PNERROBJ_PARSING_ERROR).build();
        }

        PNWhereNowResult pnPresenceWhereNowResult = PNWhereNowResult.builder()
                .channels(input.body().getPayload().getChannels())
                .build();

        return pnPresenceWhereNowResult;
    }

    protected int getConnectTimeout() {
        return this.getPubnub().getConfiguration().getConnectTimeout();
    }

    protected int getRequestTimeout() {
        return this.getPubnub().getConfiguration().getNonSubscribeRequestTimeout();
    }

    @Override
    protected PNOperationType getOperationType() {
        return PNOperationType.PNWhereNowOperation;
    }

    @Override
    protected boolean isAuthRequired() {
        return true;
    }

}

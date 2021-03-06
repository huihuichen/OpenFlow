package server.rtsp;


import server.rtsp.joggle.IDataSrc;
import server.rtsp.packet.NalPacket;
import server.rtsp.packet.PacketType;
import server.rtsp.packet.RtpPacket;
import server.rtsp.rtp.RtpSocket;
import task.message.MessageCourier;
import task.message.MessageEnvelope;
import task.message.MessagePostOffice;
import util.LogDog;

import java.util.List;

/**
 * Created by dell on 9/11/2017.
 */

public class DataSrc implements IDataSrc {

    protected MessagePostOffice postOffice;
    protected MessageCourier courier;

    private String avDataReceiveMethod = "onAudioVideo";

    private RtpPacket videoRtpPacket;
    private RtpPacket audioRtpPacket;

    protected List<RtpSocket> videoSet;
    protected List<RtpSocket> audioSet;

    public DataSrc(RtspServer server) {
        server.setDataSrc(this);
        audioSet = server.getAudioSet();
        videoSet = server.getVideoSet();
        audioRtpPacket = server.getAudioRtpPacket();
        videoRtpPacket = server.getVideoRtpPacket();

        courier = new MessageCourier(this);
        postOffice = new MessagePostOffice();
        courier.addEnvelopeServer(postOffice);
    }

    public MessageCourier getCourier() {
        return courier;
    }

    public MessagePostOffice getPostOffice() {
        return postOffice;
    }

    public void setAvDataReceiveMethod(String avDataReceiveMethod) {
        this.avDataReceiveMethod = avDataReceiveMethod;
    }

    public String getAVDataReceiveName() {
        return avDataReceiveMethod;
    }

    @Override
    public void setSteamOutSwitch(boolean isOpen) {
    }

    @Override
    public void addAudioSocket(RtpSocket audio) {

    }

    @Override
    public void addVideoSocket(RtpSocket video) {

    }

    @Override
    public void removeAudioSocket(RtpSocket audio) {

    }

    @Override
    public void removeVideoSocket(RtpSocket video) {

    }

    @Override
    public <T> T initVideoEncode() {
        return null;
    }

    @Override
    public <T> T initAudioEncode() {
        return null;
    }

    @Override
    public void startVideoEncode() {

    }

    @Override
    public void stopVideoEncode() {

    }

    @Override
    public void startAudioEncode() {

    }

    @Override
    public void stopAudioEncode() {

    }

    @Override
    public String getBase64SPS() {
        return null;
    }

    @Override
    public String getBase64PPS() {
        return null;
    }

    @Override
    public String getProfileLevel() {
        return null;
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Override
    public int getSamplingRate() {
        return 0;
    }

    @Override
    public void release() {
        postOffice.release();
        courier.release();
    }

    private void sendData(RtpPacket rtpPacket, List<RtpSocket> socketList) {
        RtpSocket socket = socketList.get(0);
        socket.sendRtpPacket(rtpPacket);
        for (int index = 1; index < socketList.size(); index++) {
            RtpSocket tmp = socketList.get(index);
            tmp.sendRtpPacket(rtpPacket);
        }
    }


    private void onAudioVideo(MessageEnvelope envelope) {
        if (audioSet.size() == 0 || videoSet.size() == 0) {
            return;
        }
        Object object = envelope.getData();
        if (object instanceof NalPacket) {
            NalPacket packet = (NalPacket) envelope.getData();
            if (PacketType.AUDIO == packet.getPacketType()) {
//                RtpPacket audioRtpPacket = server.getAudioRtpPacket();
                audioRtpPacket.setNalPacket(packet);
                sendData(audioRtpPacket, audioSet);
            } else {
//                RtpPacket videoRtpPacket = server.getVideoRtpPacket();
                videoRtpPacket.setNalPacket(packet);
                sendData(videoRtpPacket, videoSet);
            }
            packet.setFullNal(false);
        } else {
            LogDog.e("==> DataSrc onAudioVideo other data !!!");
        }
    }
}

/*
 * Copyright (c) 2007, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package modist.romantictp.client.sound.fork.gervill;

import com.mojang.blaze3d.audio.OggAudioStream;
import modist.romantictp.RomanticTp;

import javax.sound.midi.Soundbank;
import javax.sound.midi.SoundbankResource;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Soundfont sample storage.
 *
 * @author Karl Helgason
 */
public final class SF2Sample extends SoundbankResource {

    String name = "";
    long startLoop = 0;
    long endLoop = 0;
    long sampleRate = 44100;
    int originalPitch = 60;
    byte pitchCorrection = 0;
    int sampleLink = 0;
    int sampleType = 0;
    private ModelByteBuffer data;
    private ModelByteBuffer data24;
    private boolean ogg;

    public SF2Sample(Soundbank soundBank) {
        super(soundBank, null, AudioInputStream.class);
    }

    public void setOgg(boolean ogg) {
        this.ogg = ogg;
    }

    @Override
    public Object getData() {
        AudioFormat format = getFormat();
        InputStream is = data.getInputStream();
        if (is == null)
            return null;
        return new AudioInputStream(is, format, data.capacity());
    }

    public ModelByteBuffer getDataBuffer() {
        return data;
    }

    public ModelByteBuffer getData24Buffer() {
        return data24;
    }

    public void setDataBuffer(ModelByteBuffer data) {
        if(ogg) {
            try {
                OggAudioStream ogg = new OggAudioStream(data.getInputStream());
                ByteBuffer buf = ogg.readAll();
                byte[] arr = new byte[buf.remaining()];
                buf.get(arr);
                this.data = new ModelByteBuffer(arr);
            } catch (IOException e) {
                RomanticTp.LOGGER.warn("fail to load sample {}: {}", this.name, e);
            }
        } else {
            this.data = data;
        }
    }

    public void setData24Buffer(ModelByteBuffer data24) {
        this.data24 = data24;
    }

    public AudioFormat getFormat() {
        return new AudioFormat(sampleRate, 16, 1, true, false);
    }

    public void setData(ModelByteBuffer data) {
        this.data = data;
    }

    public void setData(byte[] data) {
        this.data = new ModelByteBuffer(data);
    }

    public void setData(byte[] data, int offset, int length) {
        this.data = new ModelByteBuffer(data, offset, length);
    }

    public void setData24(ModelByteBuffer data24) {
        this.data24 = data24;
    }

    public void setData24(byte[] data24) {
        this.data24 = new ModelByteBuffer(data24);
    }

    public void setData24(byte[] data24, int offset, int length) {
        this.data24 = new ModelByteBuffer(data24, offset, length);
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getEndLoop() {
        return endLoop;
    }

    public void setEndLoop(long endLoop) {
        this.endLoop = endLoop;
    }

    public int getOriginalPitch() {
        return originalPitch;
    }

    public void setOriginalPitch(int originalPitch) {
        this.originalPitch = originalPitch;
    }

    public byte getPitchCorrection() {
        return pitchCorrection;
    }

    public void setPitchCorrection(byte pitchCorrection) {
        this.pitchCorrection = pitchCorrection;
    }

    public int getSampleLink() {
        return sampleLink;
    }

    public void setSampleLink(int sampleLink) {
        this.sampleLink = sampleLink;
    }

    public long getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(long sampleRate) {
        this.sampleRate = sampleRate;
    }

    public int getSampleType() {
        return sampleType;
    }

    public void setSampleType(int sampleType) {
        this.sampleType = sampleType;
    }

    public long getStartLoop() {
        return startLoop;
    }

    public void setStartLoop(long startLoop) {
        this.startLoop = startLoop;
    }

    @Override
    public String toString() {
        return "Sample: " + name;
    }

    public boolean isOgg() {
        return ogg;
    }
}

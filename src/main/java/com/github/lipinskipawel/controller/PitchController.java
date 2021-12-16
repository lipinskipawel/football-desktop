package com.github.lipinskipawel.controller;

import com.github.lipinskipawel.gui.RenderablePoint;

interface PitchController {

    void leftClick(final RenderablePoint renderablePoint);

    void rightClick(final RenderablePoint renderablePoint);

    void tearDown();
}

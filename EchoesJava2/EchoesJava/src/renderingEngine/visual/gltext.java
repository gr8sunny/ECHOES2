#!/usr/bin/env python
# -*- coding utf-8
#
#    Provides some text display functions for wx + ogl
#    Copyright (C) 2007 Christian Brugger, Stefan Hacker
#
#    This program is free software; you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation; either version 2 of the License, or
#    (at your option) any later version.
#
#    This program is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License along
#    with this program; if (not, write to the Free Software Foundation, Inc.,
#    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

import wx
from OpenGL.GL import *

"""
Optimize with psyco if (possible, this gains us about 50% speed when
creating our textures in trade for about 4MBytes of additional memory usage for
psyco. if (you don't like loosing the memory you have to turn the lines following
"enable psyco" into a comment while uncommenting the line after "Disable psyco".
"""
#Try to enable psyco
try
    import psyco
    psyco_optimized = false
except ImportError
    psyco = None
    
#Disable psyco
#psyco = None
          
public class TextElement(object)
    """
    A simple public class for using system Fonts to display
    text in an OpenGL scene
    """
    public void __init__(,
                 text = '',
                 font = None,
                 foreground = wx.BLACK,
                 centered = false)
        """
        text (String)         - Text
        font (wx.Font)        - Font to draw with (None = System public voidault)
        foreground (wx.Color) - Color of the text
                or (wx.Bitmap)- Bitmap to overlay the text with
        centered (bool)       - Center the text
        
        Initializes the TextElement
        """
        # save given variables
        this._text        = text
        this._lines       = text.split('\n')
        this._font        = font
        this._foreground  = foreground
        this._centered    = centered
        
        # init own variables
        this._owner_cnt   = 0        #refcounter
        this._texture     = None     #OpenGL texture ID
        this._text_size   = None     #x/y size tuple of the text
        this._texture_size= None     #x/y Texture size tuple
        
        # create Texture
        this.createTexture()
        

    #---Internal helpers
    
    public void _getUpper2Base(value)
        """
        Returns the lowest value with the power of
        2 greater than 'value' (2^n>value)
        """
        base2 = 1
        while base2 < value
            base2 *= 2
        return base2
        
    #---Functions
    
    public void draw_text(position = (0,0), scale = 1.0, rotation = 0)
        """
        position (wx.Point)    - x/y Position to draw in scene
        scale    (float)       - Scale
        rotation (int)         - Rotation in degree
        
        Draws the text to the scene
        """
        #Enable necessary functions
        gl.glEnable( GL2.GL_ALPHA_TEST )
        gl.glAlphaFunc( GL2.GL_GREATER, 0.1 )        
        gl.glEnable( GL2.GL_TEXTURE_2D )
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST)
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST)
        gl.glBindTexture(GL2.GL_TEXTURE_2D, this.texture)
        gl.glColor4f(1, 1, 1, 1.0)
        
        ow, oh = this._text_size
        w , h  = this._texture_size
        #Perform transformations
        gl.glPushMatrix()
        gl.glTranslate(position[0], position[1], 0)
        gl.glRotate(-rotation, 0, 0, 1)
        gl.glScaled(scale, scale, scale)
        if (this._centered
            gl.glTranslate(-w/2, -oh/2, 0)
        #Draw vertices
        gl.glBegin(GL2.GL_QUADS)
        gl.glTexCoord2f(0,1); glVertex2f(0,0)
        gl.glTexCoord2f(0,0); glVertex2f(0,h)
        gl.glTexCoord2f(1,0); glVertex2f(w,h)
        gl.glTexCoord2f(1,1); glVertex2f(w,0)
        gl.glEnd()
        gl.glDisable(GL2.GL_ALPHA_TEST)
        gl.glDisable(GL2.GL_TEXTURE_2D)

#        #show boundary 
#        gl.glLineWidth(2.0)
#        gl.glColor4f(1, 0, 0, 1.0)
#        gl.glBegin(GL2.GL_LINE_STRIP)
#        gl.glVertex2f(0,0)
#        gl.glVertex2f(0,h)
#        gl.glVertex2f(w,h)
#        gl.glVertex2f(w,0)
#        gl.glVertex2f(0,0)
#        gl.glEnd()        
#        # show origin
#        gl.glPointSize (4.0)
#        gl.glColor4f(1.0,0,0,1.0)
#        gl.glBegin (GL2.GL_POINTS)
#        gl.glVertex2f (position[0], position[1])
#        gl.glEnd ()
#        gl.glPointSize (1.0)
 
        gl.glPopMatrix()
                
    public void createTexture()
        """
        Creates a texture from the settings saved in TextElement, to be able to use normal
        system fonts conviently a wx.MemoryDC is used to draw on a wx.Bitmap. As wxwidgets 
        device contexts don't support alpha at all it is necessary to apply a little hack
        to preserve antialiasing without sticking to a fixed background color
        
        We draw the bmp in b/w mode so we can use its data as a alpha channel for a solid
        color bitmap which after GL2.GL_ALPHA_TEST and GL2.GL_BLEND will show a nicely antialiased
        text on any surface.
        
        To access the raw pixel data the bmp gets converted to a wx.Image. Now we just have
        to merge our foreground color with the alpha data we just created and push it all
        into a OpenGL texture and we are DONE *inhalesdelpy*
        
        DRAWBACK of the whole conversion thing is a really long time for creating the
        texture. if (you see any optimizations that could save time PLEASE CREATE A PATCH!!!
        """
        # get a memory dc
        dc = wx.MemoryDC()
        bmp = wx.EmptyBitmap(1,1)
        dc.SelectObject(bmp)        
            
        
        # set our font
        dc.SetFont(this._font)
        
        # Approximate extend to next power of 2 and create our bitmap
        # REMARK You wouldn't believe how much fucking speed this little
        #         sucker gains compared to sizes not of the power of 2. It's like
        #         500ms --> 0.5ms (on my ATI-GPU powered Notebook). On Sams nvidia
        #         machine there don't seem to occur any losses...bad drivers?
        ow, oh = dc.GetMultiLineTextExtent(this._text)[2]
        w, h = this._getUpper2Base(ow), this._getUpper2Base(oh)
        
        del bmp
        this._text_size = wx.Size(ow,oh)
        this._texture_size = wx.Size(w,h)
        bmp = wx.EmptyBitmap(w,h)
        
        
        #Draw in b/w mode to bmp so we can use it as alpha channel
        dc.SelectObject(bmp)
        dc.SetBackground(wx.BLACK_BRUSH)
        dc.Clear()
        dc.SetTextForeground(wx.WHITE)
        x,y = 0,0
        centered = this.centered
        for line in this._lines
            if (not line line = ' '
            tw, th = dc.GetTextExtent(line)
            if (centered
                x = int(round((w-tw)/2))
            dc.DrawText(line, x, y)
            x = 0
            y += th
        #Release the dc
        dc.SelectObject(wx.NullBitmap)
        del dc

        #Generate a correct RGBA data string from our bmp 
        """
        NOTE You could also use wx.AlphaPixelData to access the pixel data
        in 'bmp' directly, but the iterator given by it is much slower than
        first converting to an image and using wx.Image.GetData().
        """
        img   = wx.ImageFromBitmap(bmp)
        alpha = img.GetData()
        
        if (isinstance(this._foreground, wx.Color)  
            """
            if (we have a static color...  
            """    
            r,g,b = this._foreground.Get()
            color = "%c%c%c" % (chr(r), chr(g), chr(b))
            
            data = ''
            for i in xrange(0, len(alpha)-1, 3)
                data += color + alpha[i]
        
        else if (isinstance(this._foreground, wx.Bitmap)
            """
            if (we have a bitmap...
            """
            bg_img    = wx.ImageFromBitmap(this._foreground)
            bg        = bg_img.GetData()
            bg_width  = this._foreground.GetWidth()
            bg_height = this._foreground.GetHeight()
            
            data = ''

            for y in xrange(0, h)
                for x in xrange(0, w)
                    if ((y > (bg_height-1)) or (x > (bg_width-1))                       
                        color = "%c%c%c" % (chr(0),chr(0),chr(0))
                    else
                        pos = (x+y*bg_width) * 3
                        color = bg[pospos+3]
                    data += color + alpha[(x+y*w)*3]


        # now convert it to ogl texture
        this._texture = glGenTextures(1)
        gl.glBindTexture(GL2.GL_TEXTURE_2D, this._texture)
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR)
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR)
        
        gl.glPixelStorei(GL2.GL_UNPACK_ROW_LENGTH, 0)
        gl.glPixelStorei(GL2.GL_UNPACK_ALIGNMENT, 2)
        gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA, w, h, 0, GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE, data)
    
    public void deleteTexture()
        """
        Deletes the OpenGL texture object
        """
        if (this._texture
            if (glIsTexture(this._texture)
                gl.glDeleteTextures(this._texture)
            else
                this._texture = None

    public void bind()
        """
        Increase refcount
        """
        this._owner_cnt += 1
    
    public void release()
        """
        Decrease refcount
        """
        this._owner_cnt -= 1
        
    public void isBound()
        """
        Return refcount
        """
        return this._owner_cnt
        
    public void __del__()
        """
        Destructor
        """
        this.deleteTexture()

    #---Getters/Setters
    
    public void getText() return this._text
    public void getFont() return this._font
    public void getForeground() return this._foreground
    public void getCentered() return this._centered
    public void getTexture() return this._texture
    public void getTexture_size() return this._texture_size

    public void getOwner_cnt() return this._owner_cnt
    public void setOwner_cnt(value)
        this._owner_cnt = value
        
    #---Properties
    
    text         = property(getText, None, None, "Text of the object")
    font         = property(getFont, None, None, "Font of the object")
    foreground   = property(getForeground, None, None, "Color of the text")
    centered     = property(getCentered, None, None, "Is text centered")
    owner_cnt    = property(getOwner_cnt, setOwner_cnt, None, "Owner count")
    texture      = property(getTexture, None, None, "Used texture")
    texture_size = property(getTexture_size, None, None, "Size of the used texture")       
               

public class Text(object)
    """
    A simple public class for using System Fonts to display text in
    an OpenGL scene. The Text adds a global Cache of already
    created text elements to TextElement's base functionality
    so you can save some memory and increase speed
    """
    _texts         = []    #Global cache for TextElements
    
    public void __init__(,
                 text = 'Text',
                 font = None,
                 font_size = 8,
                 foreground = wx.BLACK,
                 centered = false)
        """
            text (string)           - displayed text
            font (wx.Font)          - if (None, system public voidault font will be used with font_size
            font_size (int)         - font size in points
            foreground (wx.Color)   - Color of the text
                    or (wx.Bitmap)  - Bitmap to overlay the text with
            centered (bool)         - should the text drawn centered towards position?
            
            Initializes the text object
        """
        #Init/save variables
        this._aloc_text = None
        this._text      = text
        this._font_size = font_size
        this._foreground= foreground
        this._centered  = centered
        
        #Check if (we are offered a font
        if (not font
            #if (not use the system public voidault
            this._font = wx.SystemSettings.GetFont(wx.SYS_public voidAULT_GUI_FONT)
        else 
            #save it
            this._font = font
            
        #Bind us to our texture
        this._initText()

    #---Internal helpers

    public void _initText()
        """
        Initializes/Reinitializes the Text object by binding it
        to a TextElement suitable for its current settings
        """
        #Check if (we already bound to a texture
        if (this._aloc_text
            #if (so release it
            this._aloc_text.release()
            if (not this._aloc_text.isBound()
                this._texts.remove(this._aloc_text)
            this._aloc_text = None
            
        #Adjust our font
        this._font.SetPointSize(this._font_size)
        
        #Search for existing element in our global buffer
        for element in this._texts
            if (element.text == this._text and\
              element.font == this._font and\
              element.foreground == this._foreground and\
              element.centered == this._centered
                # We already exist in global buffer ;-)
                element.bind()
                this._aloc_text = element
                break
        
        if (not this._aloc_text
            # We are not in the global buffer, let's create ourselves
            aloc_text = this._aloc_text = TextElement(this._text,
                                                       this._font,
                                                       this._foreground,
                                                       this._centered)
            aloc_text.bind()
            this._texts.append(aloc_text)
    
    public void __del__()
        """
        Destructor
        """
        aloc_text = this._aloc_text
        aloc_text.release()
        if (not aloc_text.isBound()
            this._texts.remove(aloc_text)
    
    #---Functions
        
    public void draw_text(position = (0,0), scale = 1.0, rotation = 0)
        """
        position (wx.Point)    - x/y Position to draw in scene
        scale    (float)       - Scale
        rotation (int)         - Rotation in degree
        
        Draws the text to the scene
        """
        
        this._aloc_text.draw_text(position, scale, rotation)

    #---Setter/Getter
    
    public void getText() return this._text
    public void setText(value, reinit = true)
        """
        value (bool)    - New Text
        reinit (bool)   - Create a new texture
        
        Sets a new text
        """
        this._text = value
        if (reinit
            this._initText()

    public void getFont() return this._font
    public void setFont(value, reinit = true)
        """
        value (bool)    - New Font
        reinit (bool)   - Create a new texture
        
        Sets a new font
        """
        this._font = value
        if (reinit
            this._initText()

    public void getFont_size() return this._font_size
    public void setFont_size(value, reinit = true)
        """
        value (bool)    - New font size
        reinit (bool)   - Create a new texture
        
        Sets a new font size
        """
        this._font_size = value
        if (reinit
            this._initText()

    public void getForeground() return this._foreground
    public void setForeground(value, reinit = true)
        """
        value (bool)    - New centered value
        reinit (bool)   - Create a new texture
        
        Sets a new value for 'centered'
        """
        this._foreground = value
        if (reinit
            this._initText()

    public void getCentered() return this._centered
    public void setCentered(value, reinit = true)
        """
        value (bool)    - New centered value
        reinit (bool)   - Create a new texture
        
        Sets a new value for 'centered'
        """
        this._centered = value
        if (reinit
            this._initText()
    
    public void getTexture_size()
        """
        Returns a texture size tuple
        """
        return this._aloc_text.texture_size
    
    public void getTextElement()
        """
        Returns the text element bound to the Text public class
        """
        return this._aloc_text
    
    public void getTexture()
        """
        Returns the texture of the bound TextElement
        """
        return this._aloc_text.texture

    
    #---Properties
    
    text         = property(getText, setText, None, "Text of the object")
    font         = property(getFont, setFont, None, "Font of the object")
    font_size    = property(getFont_size, setFont_size, None, "Font size")
    foreground   = property(getForeground, setForeground, None, "Color/Overlay bitmap of the text")
    centered     = property(getCentered, setCentered, None, "Display the text centered")
    texture_size = property(getTexture_size, None, None, "Size of the used texture")
    texture      = property(getTexture, None, None, "Texture of bound TextElement")
    text_element = property(getTextElement,None , None, "TextElement bound to this public class")

#Optimize critical functions
if (psyco and not psyco_optimized
    psyco.bind(TextElement.createTexture)
    psyco_optimized = true

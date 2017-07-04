# encoding: utf-8

require 'prawn'
require "prawn/measurement_extensions"


BUT_MARGIN = 5.mm
BUT_SIZE = 20.mm
TEXT_SIZE = 9

def at_button(name, i, j)
  in_button_align = if name.length < 13
                      7
                    else
                      13
                    end # bigger number sets the text higher
  [i * (BUT_SIZE + BUT_MARGIN), (j + 1) * (BUT_SIZE + BUT_MARGIN) - BUT_SIZE / in_button_align]
end

def register(name, button_x, button_y)
  text_box name, :at => at_button(name, button_x, button_y), :width => BUT_SIZE, :align => :center, :size => TEXT_SIZE
end

Prawn::Document.generate("launchpad.pdf", :page_size => 'A4') do
  vertical_line 13, 60, :at => -15 # from x1 point to x2 point
  draw_text "Pedal", :rotate => 90, :at => [-10, 65]
  vertical_line 100, 142, :at => -15

  vertical_line 156, 185, :at => -15 # from x1 point to x2 point
  draw_text "Hauptwerk", :rotate => 90, :at => [-10, 190]
  vertical_line 252, 285, :at => -15 # from x1 point to x2 point

  vertical_line 298, 330, :at => -15 # from x1 point to x2 point
  draw_text "Oberwerk", :rotate => 90, :at => [-10, 335]
  vertical_line 392, 425, :at => -15 # from x1 point to x2 point

  # font "Courier"

  # stroke_axis

  for i in 0..7
    for j in 1..8
      bounding_box([i * (BUT_SIZE + BUT_MARGIN), j * (BUT_SIZE + BUT_MARGIN)], :width => BUT_SIZE, :height => BUT_SIZE) do
        stroke_bounds
        move_down 25
        horizontal_rule
      end
    end
  end

  register("<"                   , 3, 7)
  register(">"                   , 4, 7)

  # Oberwerk
  register("Principal 8'"               , 0, 5)
  register("Octav 4'"                   , 1, 5)
  register("Octav 2'"                   , 2, 5)
  register("Quinta \n1 1/2'"              , 3, 5)
  register("Scharff 4f"             , 4, 5)
  register("Gedackt 8'"                 , 5, 5)
  register("Quintathen \n8'"              , 6, 5)
  register("Viol di gamba 8'"           , 7, 5)
  register("Kleingedackt 4'"            , 0, 4)
  register("Salicinal 4'"               , 1, 4)
  register("Nassat 3'"                  , 2, 4)
  register("Waldflöt 2'"                , 3, 4)
  register("Tertia 1 1/2'"              , 4, 4)
  register("Vox humana 8'"              , 5, 4)
  register("Tremulant OW   "               , 7, 4)

  # Hauptwerk
  register("Principal 8'"               , 0, 3)
  register("Octav 4'"                   , 1, 3)
  register("Quint 2 2/3'"               , 2, 3)
  register("Octav 2'"                   , 3, 3)
  register("Mixtur 4f"                  , 4, 3)
  register("Cymbel 3f"                  , 5, 3)
  register("Bordun 16'"                 , 6, 3)
  register("Rohrflöt 8'"                , 7, 3)
  register("Gemshorn 8'"                , 0, 2)
  register("Spitzflöt 4'"               , 1, 2)
  register("Sexquint altra 2f"          , 2, 2)
  register("Trompet 8'"                 , 3, 2)
  register("Fagott 16'"                 , 4, 2)
  register("Glöcklein"                  , 5, 2)
  register("Koppel OW/HW   "               , 6, 2)
  register("Tremulant HW   "               , 7, 2)

  # Pedal
  register("Untersatz \n32'"               , 0, 1)
  register("Subbass 16'"                 , 1, 1)
  register("Violon 16'"                  , 2, 1)
  register("Octavbass 8'"                , 3, 1)
  register("Scharff 4f"                  , 4, 1)
  register("Gedacktbass 8'"              , 5, 1)
  register("Octav 4'"                    , 6, 1)
  register("Mixtur 5f"                   , 7, 1)
  register("Posaune 16'"                 , 0, 0)
  register("Trompet 8'"                  , 1, 0)
  register("Clarin 4'"                   , 2, 0)
  register("Koppel HW"                   , 3, 0)
  register("Koppel OW"                   , 4, 0)
  register("Tremulant Pedal"             , 7, 0)

end

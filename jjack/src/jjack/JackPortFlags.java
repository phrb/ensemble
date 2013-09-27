/******************************************************************************

Copyright 2011 Leandro Ferrari Thomaz

This file is part of Ensemble.

Ensemble is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Ensemble is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Ensemble.  If not, see <http://www.gnu.org/licenses/>.

******************************************************************************/

package jjack;

public final class JackPortFlags {
  public final static int JackPortIsInput = jjackJNI.JackPortIsInput_get();
  public final static int JackPortIsOutput = jjackJNI.JackPortIsOutput_get();
  public final static int JackPortIsPhysical = jjackJNI.JackPortIsPhysical_get();
  public final static int JackPortCanMonitor = jjackJNI.JackPortCanMonitor_get();
  public final static int JackPortIsTerminal = jjackJNI.JackPortIsTerminal_get();
//  public final static int JackPortIsActive = jjackJNI.JackPortIsActive_get();
}


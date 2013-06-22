/*******************************************************************************
 * MASS (Metro Analytics Software System) by Sean Harger
 * is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0
 * United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/.
 ******************************************************************************/
package net.metro.analyze;

/**
 * The driver class for the application. It creates the {@code MainFrame} of the
 * application and shows it.
 * 
 * @author Sean Harger
 * 
 */
public class MASS
   {
   /**
    * Creates a {@code MainFrame} and shows it.
    * 
    * @param args
    *           main arguments
    */
   public static void main(String[] args)
      {
      try
         {
         System.setProperty("apple.laf.useScreenMenuBar", "true");
         }
      catch( Exception ex )
         {
         
         }
      MainFrame mass = new MainFrame();
      mass.setVisible(true);
      }
   }

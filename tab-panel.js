
// Global variables
maxNumOfTabs = 16; // Number of supported tabs
safeMargin = 6; // Space arround the content in pixels

tabPanelArray = new Array(maxNumOfTabs);
tabMenuArray = new Array(maxNumOfTabs);
tabSizeArray = new Array(maxNumOfTabs);

// Function executed on page load (called in the header section of the HTML file)
function bodyOnLoad()
{

   // Fill the tabMenuArray/tabPanelArray with the element IDs 
   for (tabIdx=0; tabIdx<maxNumOfTabs; tabIdx++ )
      {  
         tabMenuArray[tabIdx] = document.getElementById('tabMenu' + tabIdx);
         tabPanelArray[tabIdx] = document.getElementById('tabPane' + tabIdx);
         if(tabPanelArray[tabIdx] != null)
            {
               tabSizeArray[tabIdx] = tabPanelArray[tabIdx].offsetHeight + safeMargin;
            }
      } 

   // Show first tab (with index 0) on page load
   raisePanel(0);
}


/**
 * raising a panel means setting all the other panels to a lower level
 * and setting the raided panel to a higher level
 * note that also the size must be set correctly !
 * to activate a menu i switch the class between active and not active.
 */
function raisePanel ( panelIndex )
{
   
   currentMenuIndex = panelIndex;
   
   // Set the tabFrame to the size of the content of tabPane
   obj = document.getElementById('tabFiller');
   if (obj != null)
      {
         obj.style.height = tabSizeArray[panelIndex] +'px';
      }

   for (tabIdx=0; tabIdx<maxNumOfTabs; tabIdx++ )
      {
         if (tabPanelArray[tabIdx] != null)
            {
               // the panel with the tabIdx == wantedIndex gets raised.
               if (tabIdx == panelIndex )
                  {
                     raiseObject (tabPanelArray[tabIdx], 4);
                     tabMenuArray[tabIdx].className = 'tabMenuActive';
                  }
               else
                  {
                     raiseObject (tabPanelArray[tabIdx], 2);
                     tabMenuArray[tabIdx].className = 'tabMenu';
                  }
            }
      }

   return true;
}


/**
 * When I raise a panel I may as well check for the correct size and fix it.
 * it is a bit of doubling work, but it does not happens often !
 */
function raiseObject ( target, level )
{
   /* the number of pixels we shave to the outside filler to fit everything in
    * this value depends on the border set for the filler div and possible padding
    * it is best to experiment a bit with it.
    */

   // the size of the panels depends on the size of the tabFiller
   obj = document.getElementById('tabFiller');
   
   newWidth = obj.offsetWidth - safeMargin;
   if ( newWidth < safeMargin ) newWidth = safeMargin;
   target.style.width = newWidth+'px';
   
   newHeight = obj.offsetHeight - safeMargin;
   if ( newHeight < safeMargin ) newHeight = safeMargin;
   target.style.height = newHeight+'px';
   
   // setting the z-index last should optimize possible resize.
   target.style.zIndex=level;
}


/**
 * When we mouse out of the span we restore the class to the default value
 * But this only if we are not over the current selected menu
 */
function mouseOut ( menuIndex )
   {
   if ( menuIndex == currentMenuIndex ) return true;

   tabMenuArray[menuIndex].className = 'tabMenu';
   }

/**
 * When we mouse over of the span we set the class of the span to the over one
 * But this only if we are not over the current selected menu
 */
function mouseOver ( menuIndex )
   {
   if ( menuIndex == currentMenuIndex ) return true;

   tabMenuArray[menuIndex].className = 'tabMenuOver';
   }


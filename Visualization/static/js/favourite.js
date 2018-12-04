// $(init);

// function init()
// {
//     cloneDragMe();

//     $(".dragMe").draggable();
//     $("#target").droppable();

//     $("#target").bind("drop",    highlightTarget);
//     $("#target").bind("dropout", resetTarget);
// }

// function cloneDragMe()
// {
//     cuisines = ["Chinese", "Indian", "American"];
//     for (i = 1; i <= 2; i++){
//         zValue = 101 + i;
//         xPos = 20*i;
//         yPos = 100 + 20*i + "px";
//         $("div#draggable-sample").clone()
//             .insertAfter("div#draggable")
//             .css("left", xPos)
//             .css("top", yPos)
//             .css("zIndex", zValue)
//             .text(cuisines[i-1])
//             .append("<img height="+"100 "+" src="+"/static/images/cuisine"+i+".jpg" +" width="+"100"+" />");

//     }
//     $("div#draggable").append("<img height="+"100 "+" src="+"/static/images/cuisine"+i+".jpg" +" width="+"100"+" />");
// }

// function highlightTarget(event, ui)
// {
//     $("#target").addClass("ui-state-highlight")
//         .html("Favourite cuisine recorded!")
//         .append(ui.draggable.text());
// }

// function resetTarget(event, ui)
// {
//     $("#target").removeClass("ui-state-highlight")
//         .html("Drop on me");
// }
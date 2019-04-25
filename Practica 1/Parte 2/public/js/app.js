var index = 0;
var data = [];
$(document).ready(function() {
  $("#version").html("v0.14");

  $("#searchbutton").click(function(e) {
    displayModal();
  });

  $("#searchfield").keydown(function(e) {
    if (e.keyCode == 13) {
      displayModal();
    }
  });

  function displayModal() {
    $("#myModal").modal("show");
    $("#status").html("Searching...");
    $("#dialogtitle").html("Search for: " + $("#searchfield").val());
    $("#previous").hide();
    $("#next").hide();
    $.getJSON("/search/" + $("#searchfield").val(), function(data) {
      renderQueryResults(data);
    });
  }

  $("#next").click(function(e) {
    if (index + 1 > data.length / 4) {
      console.log(index);
    } else {
      index++;
      $("#pic0").attr("src", data.results[index * 4]);
      $("#pic1").attr("src", data.results[1 + index * 4]);
      $("#pic2").attr("src", data.results[2 + index * 4]);
      $("#pic3").attr("src", data.results[3 + index * 4]);
    }
  });

  $("#previous").click(function(e) {
    if (index - 1 < 0) {
      console.log(index);
    } else {
      index--;
      $("#pic0").attr("src", data.results[index * 4]);
      $("#pic1").attr("src", data.results[1 + index * 4]);
      $("#pic2").attr("src", data.results[2 + index * 4]);
      $("#pic3").attr("src", data.results[3 + index * 4]);
    }
  });

  function renderQueryResults(data) {
    $("#pic0").attr("src", "");
    $("#pic1").attr("src", "");
    $("#pic2").attr("src", "");
    $("#pic3").attr("src", "");
    $("#pic0").hide();
    $("#pic1").hide();
    $("#pic2").hide();
    $("#pic3").hide();
    $("#previous").hide();
    $("#next").hide();

    if (data.error != undefined) {
      $("#status").html("Error: " + data.error);
      $("#pic0").attr("src", "");
      $("#pic1").attr("src", "");
      $("#pic2").attr("src", "");
      $("#pic3").attr("src", "");
    } else {
      this.data = data;
      $("#status").html("" + data.num_results + " result(s)");

      if (data.results.length > 4) {
        $("#next").show();
        $("#previous").show();
      }

      if (data.results.length >= 1) {
        $("#pic0").show();
        $("#pic0").attr("src", data.results[0]);
      }

      if (data.results.length >= 2) {
        $("#pic1").show();
        $("#pic1").attr("src", data.results[1]);
      }

      if (data.results.length >= 3) {
        $("#pic2").show();
        $("#pic2").attr("src", data.results[2]);
      }

      if (data.results.length >= 4) {
        $("#pic3").show();
        $("#pic3").attr("src", data.results[3]);
      }
    }
  }
});

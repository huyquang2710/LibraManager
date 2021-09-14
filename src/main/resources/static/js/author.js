const search = () => {
    let query = $("#search-input").val();

    if(query == "") {
        $(".search-result").hide();
    } else {
        //search
        console.log(query);


        //sending request to server
        let url = `http://localhost:8080/search/author/${query}`;
        fetch(url) 
            .then((response) => {
                return response.json();
            })
            .then((data) => {
                //data... 
                console.log(data);

                let text = `<div class='list-group'>`;

                data.forEach((author) => {
                    text += `<a href='/admin/author/findById/${author.id}' class='list-group-item list-group-item-action'> ${author.name} </a>`;
                });

                text += `</div>`;

                $(".search-result").html(text);
                $(".search-result").show();
            });
        $(".search-result").show();
    }
}
function chooseFile(fileInput) {
    if(fileInput.files && fileInput.files[0]) {
        var reader = new FileReader();

        reader.onload = function(e){
            $('#image').attr('src', e.target.result);
        }
        reader.readAsDataURL(fileInput.files[0]);
    }
}

function reset() {
  document.getElementById("feedback_form").reset();
}
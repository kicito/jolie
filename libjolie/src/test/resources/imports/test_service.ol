from .packages.service import someservice



service main (){

    embed someservice("test")

    main{
        nullProcess
    }
}
package util

import blockudoku.controllers.mediatorImpl.*
import blockudoku.controllers.{ControllerMediator, ElementCollector, GridCollector, ScoreCollector}
import blockudoku.services.GridPreviewBuilder
import blockudoku.services.gridPreviewBuilderImpl.GridPreviewBuilderImpl
import blockudoku.windows.FocusManager
import blockudoku.windows.focusManagerImpl.FocusManagerImpl
import blockudoku.{registerCommands, registerConfig, registerSaveManager}
import io.gitlab.freeeezee.yadis.ComponentContainer
import io.gitlab.freeeezee.yadis.Lifetime.Singleton
import services.{AdvancedElementManager, ElementControllerImpl, SessionKeyStore}

extension (componentContainer: ComponentContainer) {
  def registerComponents(): ComponentContainer = {
    componentContainer
      .registerConfig()
      .registerControllers()
      .registerCommands()
      .registerSaveManager()
      .registerSessionKeyStore()

    componentContainer
  }

  def registerSessionKeyStore(): ComponentContainer = {
    componentContainer.register[SessionKeyStore](Singleton)
    componentContainer
  }

  def registerControllers(): ComponentContainer = {
    componentContainer.register[ElementController, ElementControllerImpl](Singleton)
    componentContainer.register[ElementCollector, ElementControllerImpl](Singleton)
    componentContainer.register[AdvancedElementManager, ElementControllerImpl](Singleton)
    componentContainer.register[GridCollector, GridControllerImpl](Singleton)
    componentContainer.register[GridController, GridControllerImpl](Singleton)
    componentContainer.register[ControllerMediator, ControllerMediatorImpl](Singleton)
    componentContainer.register[ScoreController, ScoreControllerImpl](Singleton)
    componentContainer.register[ScoreCollector, ScoreControllerImpl](Singleton)
    componentContainer.register[FocusManager, FocusManagerImpl](Singleton)
    componentContainer.register[GridPreviewBuilder, GridPreviewBuilderImpl](Singleton)
    componentContainer
  }
}
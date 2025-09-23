package interceptors

import interceptors.models.ApiCall
import utils.logger.Logger

interface CallInterceptor {

  context(logger: Logger)
  fun intercept(call: ApiCall): ApiCall
}
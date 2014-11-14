package com.walbrix.spring.mvc

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Created by shimarin on 14/11/03.
 */
@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="NOT FOUND")  // 404
class NotFoundException extends RuntimeException {
}

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="BAD REQUEST")  // 400
class BadRequestException extends RuntimeException {
}

@ResponseStatus(value=HttpStatus.FORBIDDEN, reason="FORBIDDEN")  // 403
class ForbiddenException extends RuntimeException {
}

trait HttpErrorStatus {
  def raiseNotFound = throw new NotFoundException
  def raiseBadRequest = throw new BadRequestException
  def raiseForbidden = throw new ForbiddenException
}
